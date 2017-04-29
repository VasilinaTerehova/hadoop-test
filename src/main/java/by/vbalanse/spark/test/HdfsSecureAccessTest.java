package by.vbalanse.spark.test;

import com.sun.security.auth.module.Krb5LoginModule;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Vasilina on 26.03.2017.
 */
public class HdfsSecureAccessTest extends javax.security.auth.login.Configuration {
    private final AppConfigurationEntry[] appConfigurationEntries;

    public HdfsSecureAccessTest(AppConfigurationEntry[] appConfigurationEntries) {
        this.appConfigurationEntries = appConfigurationEntries;
    }

    public static void main(String[] args) throws IOException, LoginException {


        //System.out.println(success);
        getFileSystem();
    }

    public static void getFileSystem() throws IOException, LoginException {
        final Configuration conf = new Configuration();
        conf.addResource("core-site.xml");
        conf.addResource("hdfs-site.xml");
        LoginContext login = login();
        //UserGroupInformation.setConfiguration(conf);
        //UserGroupInformation.loginUserFromSubject( login.getSubject() );
        //UserGroupInformation.setLoginUser(userGroupInformation);
        try {
            FileSystem fileSystem = FileSystem.get(conf);
            boolean success = fileSystem.mkdirs(new Path("/user/devuser/configs"));
            System.out.println(success);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Subject.doAs(login.getSubject(), new PrivilegedAction<Object>() {
//            @Override
//            public Object run() {
//
//                return null;
//            }
//        });


    }

    public static LoginContext login() throws LoginException, IOException {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        SecurityUtil.setAuthenticationMethod( UserGroupInformation.AuthenticationMethod.KERBEROS, conf );
        UserGroupInformation.setConfiguration( conf );
        LoginContext loginContext = getLoginContextFromUsernamePassword("devuser@PENTAHOQA.COM", "password");
        loginContext.login();
        UserGroupInformation.loginUserFromSubject( loginContext.getSubject() );
        return loginContext;
    }

    public static LoginContext getLoginContextFromUsernamePassword(final String principal, final String password)
            throws LoginException {
        Map<String, String> opts = new HashMap<String, String>( getLoginConfigOptsKerberosUser() );
        opts.put( "principal", principal );
        AppConfigurationEntry[] appConfigurationEntries =
                new AppConfigurationEntry[] { new AppConfigurationEntry( Krb5LoginModule.class.getName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, opts ),
                        new AppConfigurationEntry( UserGroupInformation.HadoopLoginModule.class.getName(),
                                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, opts ) };
        return new LoginContext( "pentaho", getSubject(), new CallbackHandler() {

            @Override
            public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
                for ( Callback callback : callbacks ) {
                    if ( callback instanceof NameCallback) {
                        ( (NameCallback) callback ).setName( principal );
                    } else if ( callback instanceof PasswordCallback) {
                        ( (PasswordCallback) callback ).setPassword( password.toCharArray() );
                    } else {
                        throw new UnsupportedCallbackException( callback );
                    }
                }
            }
        }, new HdfsSecureAccessTest( appConfigurationEntries ) );
    }

    private static Subject getSubject() throws LoginException {
        Subject subject = new Subject();

   /* try{
      UserGroupInformation.loginUserFromSubject( subject );
    } catch ( IOException e ) {
      throw new LoginException( e.getMessage() );
    }*/
        return subject;
    }

    public LoginContext getLoginContextFromKerberosCache( String principal ) throws LoginException {
        Map<String, String> opts = new HashMap<String, String>( getLoginConfigOptsKerberosNoPassword() );
        opts.put( "principal", principal );
        AppConfigurationEntry[] appConfigurationEntries =
                new AppConfigurationEntry[] { new AppConfigurationEntry( Krb5LoginModule.class.getName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, opts ),
                        new AppConfigurationEntry( UserGroupInformation.class.getName(),
                                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, opts ) };
        return new LoginContext( "pentaho", getSubject(), null, new HdfsSecureAccessTest(
                appConfigurationEntries ) );
    }

    private static Map<String, String> getLoginConfigOptsKerberosNoPassword() {
        Map<String, String> result = new HashMap<String, String>( getLoginConfigOptsKerberosUser() );
        result.put( "useTicketCache", Boolean.TRUE.toString() );
        result.put( "renewTGT", Boolean.TRUE.toString() );
        // Never prompt for passwords
        result.put( "doNotPrompt", Boolean.TRUE.toString() );
        return result;
    }

    private static Map<String, String> getLoginConfigOptsKerberosUser() {
        Map<String, String> result = new HashMap<String, String>( createLoginConfigBaseMap() );
        result.put( "useTicketCache", Boolean.FALSE.toString() );
        // Attempt to renew tickets
        result.put( "renewTGT", Boolean.FALSE.toString() );
        return Collections.unmodifiableMap( result );
    }

    private static Map<String, String> createLoginConfigBaseMap() {
        Map<String, String> result = new HashMap<String, String>();
        // Enable JAAS debug if PENTAHO_JAAS_DEBUG is set
        // if ( Boolean.parseBoolean( System.getenv( "PENTAHO_JAAS_DEBUG" ) ) ) {
        result.put( "debug", Boolean.TRUE.toString() );
        //}

        return Collections.unmodifiableMap( result );
    }

    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        return appConfigurationEntries;
    }
}

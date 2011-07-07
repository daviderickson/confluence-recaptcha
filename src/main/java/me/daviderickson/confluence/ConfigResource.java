package me.daviderickson.confluence;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

@Path("/")
public class ConfigResource {
  private final UserManager userManager;
  private final PluginSettingsFactory pluginSettingsFactory;
  private final TransactionTemplate transactionTemplate;
  private static String KEY = "recaptcha";

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static final class Config
  {
      @XmlElement private String publicKey;
      @XmlElement private String privateKey;
      /**
       * @return the publicKey
       */
      public String getPublicKey() {
        return publicKey;
      }
      /**
       * @param publicKey the publicKey to set
       */
      public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
      }
      /**
       * @return the privateKey
       */
      public String getPrivateKey() {
        return privateKey;
      }
      /**
       * @param privateKey the privateKey to set
       */
      public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
      }
  }

  public ConfigResource(UserManager userManager,
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate) {
    this.userManager = userManager;
    this.pluginSettingsFactory = pluginSettingsFactory;
    this.transactionTemplate = transactionTemplate;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@Context HttpServletRequest request)
  {
      String username = userManager.getRemoteUsername(request);
      if (username == null || (username != null && !userManager.isSystemAdmin(username)))
      {
          return Response.status(Status.UNAUTHORIZED).build();
      }

      return Response.ok(transactionTemplate.execute(new TransactionCallback()
      {
          public Object doInTransaction()
          {
              PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
              Config config = new Config();
              config.setPublicKey((String) settings.get(KEY + ".publicKey"));
              config.setPrivateKey((String) settings.get(KEY + ".privateKey"));
              return config;
          }
      })).build();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put(final Config config, @Context HttpServletRequest request)
  {
      String username = userManager.getRemoteUsername(request);
      if (username == null || (username != null && !userManager.isSystemAdmin(username)))
      {
          return Response.status(Status.UNAUTHORIZED).build();
      }

      transactionTemplate.execute(new TransactionCallback()
      {
          public Object doInTransaction()
          {
              PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
              pluginSettings.put(KEY + ".publicKey", config.getPublicKey());
              pluginSettings.put(KEY  +".privateKey", config.getPrivateKey());
              return null;
          }
      });
       
      return Response.noContent().build();
  }
}

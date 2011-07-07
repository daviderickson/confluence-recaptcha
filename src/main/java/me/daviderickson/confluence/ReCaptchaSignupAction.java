/**
 * @author David Erickson (derickso@stanford.edu) - Jul 5, 2011
 */
package me.daviderickson.confluence;

import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import com.atlassian.confluence.user.actions.SignupAction;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.opensymphony.webwork.ServletActionContext;

/**
 * @author David Erickson (derickso@stanford.edu) - Jul 5, 2011
 */
public class ReCaptchaSignupAction extends SignupAction {
  private static final long serialVersionUID = 1934527739429303433L;
  public PluginSettingsFactory pluginSettingsFactory;
  protected String recaptcha_challenge_field;
  protected String recaptcha_response_field;

  /**
   * @param recaptcha_challenge_field the recaptcha_challenge_field to set
   */
  public void setRecaptcha_challenge_field(String recaptcha_challenge_field) {
    this.recaptcha_challenge_field = recaptcha_challenge_field;
  }

  /**
   * @param recaptcha_response_field the recaptcha_response_field to set
   */
  public void setRecaptcha_response_field(String recaptcha_response_field) {
    this.recaptcha_response_field = recaptcha_response_field;
  }

  /**
   * 
   */
  public ReCaptchaSignupAction() {
    super();
  }

  @Override
  public void validate() {
    super.validate();
    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
    reCaptcha.setPrivateKey("6LcpWAMAAAAAAETX5jHd9NlPtIHJA4G-ra3X2RQB");
    HttpServletRequest request = ServletActionContext.getRequest();
    ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(
        request.getRemoteAddr(),
        recaptcha_challenge_field, recaptcha_response_field);
    if (!reCaptchaResponse.isValid()) {
      addFieldError("captcha", "captcha.response.failed", new String[0]);
    }

  }

  /**
   * @return the pluginSettingsFactory
   */
  public PluginSettingsFactory getPluginSettingsFactory() {
    return pluginSettingsFactory;
  }

  /**
   * @param pluginSettingsFactory the pluginSettingsFactory to set
   */
  public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
    this.pluginSettingsFactory = pluginSettingsFactory;
  }
}

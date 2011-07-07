/**
 * @author David Erickson (derickso@stanford.edu) - Jul 6, 2011
 */
package me.daviderickson.confluence;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

/**
 * @author David Erickson (derickso@stanford.edu) - Jul 6, 2011
 */
public class ConfigServlet extends HttpServlet {
  private static final long serialVersionUID = 4978158109504149854L;
  private final LoginUriProvider loginUriProvider;
  private final TemplateRenderer renderer;
  private final UserManager userManager;

  public ConfigServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer) {
    this.loginUriProvider = loginUriProvider;
    this.renderer = renderer;
    this.userManager = userManager;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String username = userManager.getRemoteUsername(req);
    if (username != null && !userManager.isSystemAdmin(username)) {
      redirectToLogin(req, resp);
      return;
    }
    resp.setContentType("text/html;charset=utf-8");
    renderer.render("/custom/config.vm", resp.getWriter());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    super.doPost(req, resp);
  }

  private void redirectToLogin(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request))
        .toASCIIString());
  }

  private URI getUri(HttpServletRequest request) {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null) {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }
}

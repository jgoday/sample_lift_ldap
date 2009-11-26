package bootstrap.liftweb

import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

// lift ldap
import net.liftweb.ldap.SimpleLDAPVendor

// LoginUtils && User object
import com.sample.lib.LoginUtil
import com.sample.model.User

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.sample")

    LiftRules.dispatch.prepend(NamedPF("Login Validation") {
        case Req("group_required" :: page, extension, _) if !LoginUtil.hasAuthority_?("sample_group") =>
                LoginUtil.redirectIfLogged("/login/group_not_allowed")
        case Req("login_required" :: page , extension, _) if (!LoginUtil.isLogged) =>
                () => Full(RedirectResponse("/user_mgt/login"))
    })

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) ::
                  Menu(Loc("Restricted Login", List("login_required"), "Login required")) ::
                  Menu(Loc("Restricted Group", List("group_required"), "Group required")) ::
                  Menu(Loc("Group not allowed", List("login", "group_not_allowed"), "Group not allowd", List(Loc.Hidden))) ::
                  User.sitemap

    LiftRules.setSiteMap(SiteMap(entries:_*))

    // Initialize LDAP properties
    try {

        SimpleLDAPVendor.parameters = () =>
            SimpleLDAPVendor.parametersFromStream(
                this.getClass().getClassLoader().getResourceAsStream("ldap.properties"))
    }
    catch {
        case e: Exception => e.printStackTrace
    }

  }
}


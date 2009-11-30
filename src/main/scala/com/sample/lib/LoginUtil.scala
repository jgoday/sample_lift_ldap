package com.sample.lib


import net.liftweb.http.{LiftResponse, RedirectResponse, OkResponse}
import net.liftweb.common.{Box, Full}

import com.sample.model.User

object LoginUtil {
    def isLogged = User.loggedIn_?

    def hasAuthority_?(name: String) : Boolean = {
        LoginUtil.isLogged && (User.getRoles.count((element: String) => {element == name}) > 0)
    }

    def redirectIfLogged(path: String) : () => Box[LiftResponse] = {
        if(!LoginUtil.isLogged) {
            () => Full(RedirectResponse("/user_mgt/login"))
        }
        else {
            () => Full(RedirectResponse(path))
        }
    }
}



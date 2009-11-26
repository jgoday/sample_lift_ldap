package com.sample.model

import scala.util.matching.{Regex}
import scala.xml.{NodeSeq}

// lift ldap
import net.liftweb.ldap.{LDAPProtoUser, MetaLDAPProtoUser, LDAPVendor, SimpleLDAPVendor}

import net.liftweb.common.{Box, Full}
import net.liftweb.http.{S, SessionVar}
import net.liftweb.mapper.{KeyedMetaMapper}

object roles extends SessionVar[List[String]](List())

class User extends LDAPProtoUser[User] {
    def getSingleton = User

    def getRoles: List[String] = {
        return roles.get
    }
}

object User extends User with MetaLDAPProtoUser[User] {

    override def screenWrap = Full(<lift:surround with="default" at="content">
                   <lift:bind />
    </lift:surround>)

    override def dbTableName = "tmp_users"

    override def login : NodeSeq = {
        val groupNameRx = new Regex(".*cn=(.*),ou=.*")

        def getGroupNameFromDn(dn: String): String = {
            val groupNameRx(groupName) = dn
            return groupName
        }

        def setRoles(userDn: String, ldapVendor: LDAPVendor): AnyRef = {
            // buscamos o grupo do usuario
            val filter = "(&(objectclass=groupofnames)(member=" + userDn + "))"

            val groups = ldapVendor.search(filter)
            groups.foreach(g => {
                roles.set(roles.get + getGroupNameFromDn(g))
            })
        }

        login(setRoles _)
    }
}

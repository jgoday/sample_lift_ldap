package com.sample.model

import scala.util.matching.{Regex}
import scala.xml.{NodeSeq}

// lift ldap
import net.liftweb.ldap.{LDAPProtoUser, MetaLDAPProtoUser, LDAPVendor, SimpleLDAPVendor}

import net.liftweb.common.{Box, Full}
import net.liftweb.http.{S}
import net.liftweb.mapper.{KeyedMetaMapper}


class User extends LDAPProtoUser[User] {
    def getSingleton = User
}

object User extends User with MetaLDAPProtoUser[User] {
    override def loginErrorMessage: String = "'%s' is not a valid user or password does not match"
    override def ldapUserSearch: String = "(&(objectClass=inetOrgPerson)(uid=%s))"

    override def rolesNameRegex: String = ".*cn=(.[^,]*),.*"
    override def rolesSearchFilter: String = "(&(objectclass=groupofnames)(!(cancellationdate=*))(member=%s))"

    override def screenWrap = Full(<lift:surround with="default" at="content">
                   <lift:bind />
    </lift:surround>)
}

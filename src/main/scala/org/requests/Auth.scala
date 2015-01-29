package org.requests

import com.ning.http.client.Realm
import com.ning.http.client.Realm.{ AuthScheme => AHCAuthScheme, RealmBuilder }

trait AuthScheme
object AuthScheme {
  case object DIGEST   extends AuthScheme
  case object BASIC    extends AuthScheme
  case object NTLM     extends AuthScheme
  case object SPNEGO   extends AuthScheme
  case object KERBEROS extends AuthScheme
  case object NONE     extends AuthScheme
}

case class Auth(user: String, password: String, scheme: AuthScheme) {
  private def toAHCAuthScheme(scheme: AuthScheme): AHCAuthScheme = scheme match {
    case AuthScheme.DIGEST   => AHCAuthScheme.DIGEST
    case AuthScheme.BASIC    => AHCAuthScheme.BASIC
    case AuthScheme.NTLM     => AHCAuthScheme.NTLM
    case AuthScheme.SPNEGO   => AHCAuthScheme.SPNEGO
    case AuthScheme.KERBEROS => AHCAuthScheme.KERBEROS
    case AuthScheme.NONE     => AHCAuthScheme.NONE
  }

  def toRealm: Realm =
    new RealmBuilder()
      .setPrincipal(user)
      .setPassword(password)
      .setUsePreemptiveAuth(true)
      .setScheme(toAHCAuthScheme(scheme))
      .build
}

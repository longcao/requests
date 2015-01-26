package org.requests

import com.ning.http.client.Realm
import com.ning.http.client.Realm.{ AuthScheme => NingAuthScheme, RealmBuilder }

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
  private def toNingAuthScheme(scheme: AuthScheme): NingAuthScheme = scheme match {
    case AuthScheme.DIGEST   => NingAuthScheme.DIGEST
    case AuthScheme.BASIC    => NingAuthScheme.BASIC
    case AuthScheme.NTLM     => NingAuthScheme.NTLM
    case AuthScheme.SPNEGO   => NingAuthScheme.SPNEGO
    case AuthScheme.KERBEROS => NingAuthScheme.KERBEROS
    case AuthScheme.NONE     => NingAuthScheme.NONE
  }

  def toRealm: Realm =
    new RealmBuilder()
      .setPrincipal(user)
      .setPassword(password)
      .setUsePreemptiveAuth(true)
      .setScheme(toNingAuthScheme(scheme))
      .build
}

package com.jobaroo.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*
import com.comcast.ip4s.{Host, Port}
import pureconfig.error.CannotConvert

final case class EmberConfig(host: Host, port: Port) derives ConfigReader

object EmberConfig:

  given ConfigReader[Host] = ConfigReader[String].emap { s =>
    Host.fromString(s).toRight(CannotConvert(s, Host.getClass.toString, s"Invalid host string: $s"))
  }

  given ConfigReader[Port] = ConfigReader[String].emap { s =>
    Port.fromString(s).toRight(CannotConvert(s, Port.getClass.toString, s"Invalid port: $s"))
  }
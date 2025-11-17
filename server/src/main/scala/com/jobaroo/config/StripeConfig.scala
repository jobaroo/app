package com.jobaroo.config

import scala.concurrent.duration.FiniteDuration
import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

final case class StripeConfig(key: String, price: String, successUrl: String, cancelUrl: String, webhookSecret: String) derives ConfigReader

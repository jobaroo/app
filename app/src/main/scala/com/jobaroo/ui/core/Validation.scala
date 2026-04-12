package com.jobaroo.ui.core

import cats.data.ValidatedNec

type UiValidated[A] = ValidatedNec[String, A]

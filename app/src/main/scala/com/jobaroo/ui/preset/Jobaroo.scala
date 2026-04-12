package com.jobaroo.ui.preset

import cats.syntax.semigroup.*
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.syntax.all.*

object Jobaroo:

  object shell:

    val root = Css.literal("min-h-screen bg-base-200 text-base-content")

    val inner =
      Css.literal("mx-auto flex min-h-screen w-full max-w-[1600px] flex-col gap-6 px-4 py-4 sm:px-6 lg:px-8")

    val main      = Css.literal("flex-1")
    val page      = Css.literal("flex flex-1 flex-col gap-6")
    val split     = Css.literal("grid gap-6 xl:grid-cols-[340px_minmax(0,1fr)]")
    val splitWide = Css.literal("grid gap-6 xl:grid-cols-[minmax(0,1fr)_380px]")
    val stack     = Css.literal("space-y-5")
    val gridGap5  = Css.literal("grid gap-5")

  object state:

    val centered      = Css.literal("grid place-items-center py-16 sm:py-24")
    val centeredShort = Css.literal("grid place-items-center py-12")
    val centeredTight = Css.literal("grid place-items-center py-6")
    val centeredWide  = Css.literal("grid place-items-center py-20")

  object hero:

    val root =
      Daisy.groups.heroBase |+| Css.literal("overflow-hidden rounded-[2.5rem] border border-primary/15 bg-gradient-to-br from-secondary via-neutral to-primary text-primary-content shadow-[0_30px_90px_-40px_rgba(15,23,42,0.85)]")

    val content   = Daisy.groups.heroContentBase |+| Css.literal("w-full max-w-none px-6 py-10 sm:px-10 sm:py-14")
    val grid      = Css.literal("grid w-full gap-8 lg:grid-cols-[1.35fr_0.65fr] lg:items-end")
    val textStack = Css.literal("space-y-5")

    val eyebrow = Daisy.groups.badgeBase |+| Css.of(Daisy.atoms.badgeOutline) |+| Css.literal(
      "border-white/30 bg-white/10 px-4 py-3 text-xs font-semibold uppercase tracking-[0.28em] text-white"
    )

    val title    = Css.literal("max-w-4xl text-4xl font-black leading-none sm:text-5xl lg:text-6xl")
    val subtitle = Css.literal("max-w-3xl text-base leading-8 text-white/78 sm:text-lg")
    val actions  = Css.literal("flex flex-wrap items-center gap-3 lg:justify-end")

    val counterBadge = Daisy.groups.badgeBase |+| Css.literal(
      "border-white/20 bg-white/10 px-4 py-3 text-xs font-semibold uppercase tracking-[0.24em] text-white"
    )

  object section:

    val wrap     = Css.literal("space-y-3")
    val eyebrow  = Css.literal("text-xs font-semibold uppercase tracking-[0.28em] text-primary")
    val title    = Css.literal("text-3xl font-black leading-tight text-base-content sm:text-4xl")
    val subtitle = Css.literal("max-w-2xl text-base leading-7 text-base-content/65")

  object surface:

    val card = Daisy.groups.cardBase |+| Css.literal(
      "rounded-[2rem] border border-base-300/60 bg-base-100 shadow-[0_20px_70px_-35px_rgba(15,23,42,0.45)]"
    )

    val body            = Daisy.groups.cardBodyBase |+| Css.literal("gap-5")
    val bodyPanel       = Daisy.groups.cardBodyBase |+| Css.literal("gap-5 p-5")
    val bodySpacious    = Daisy.groups.cardBodyBase |+| Css.literal("gap-8 p-6 sm:p-8 lg:p-10")
    val bodyComfortable = Daisy.groups.cardBodyBase |+| Css.literal("gap-8 p-6 sm:p-8")
    val bodyCompact     = Daisy.groups.cardBodyBase |+| Css.literal("gap-6 p-6")

    val interactive =
      Css.literal("group overflow-hidden transition duration-300 hover:-translate-y-1 hover:border-primary/35")

    val innerSoft  = Css.literal("rounded-[2rem] border border-base-300/60 bg-base-200/60 p-5 shadow-inner")
    val boxSoft    = Css.literal("rounded-[1.5rem] border border-base-300/60 bg-base-100 px-4 py-4")
    val stickyRail = Css.literal("sticky top-28")
    val fullHeight = Css.literal("h-full")

  object nav:

    val navbar =
      Daisy.groups.navbarBase |+| Css.literal("rounded-[2rem] border border-base-300/60 bg-base-100/85 px-5 py-4 shadow-[0_20px_70px_-35px_rgba(15,23,42,0.45)] backdrop-blur")

    val sticky      = Css.literal("sticky top-0 z-40")
    val start       = Css.literal("navbar-start gap-4")
    val center      = Css.literal("navbar-center hidden xl:flex")
    val end         = Css.literal("navbar-end flex items-center gap-3")
    val desktopCopy = Css.literal("hidden lg:block")
    val menu        = Daisy.groups.menuBase |+| Css.literal("gap-2 bg-base-200/50 p-1")

    val link =
      Css.literal("rounded-2xl px-4 py-2 font-semibold text-base-content/70 transition hover:bg-base-200 hover:text-base-content")

    val auxLink = Daisy.groups.linkBase |+| Css.literal("font-semibold text-primary no-underline")

    val logo =
      Css.literal("group inline-flex items-center gap-4 rounded-[1.5rem] border border-base-300/70 bg-base-100/70 px-3 py-2 transition hover:border-primary/40 hover:bg-base-200/80")

    val logoImage  = Css.literal("h-11 w-11 rounded-2xl bg-base-100 object-cover p-1 shadow-sm")
    val themeBtn   = Css.literal("btn btn-ghost rounded-2xl border border-base-300/70 bg-base-100/70 px-4")
    val sessionRow = Css.literal("hidden sm:flex items-center gap-2")
    val subtitle   = Css.literal("text-sm uppercase tracking-[0.24em] text-base-content/55")
    val title      = Css.literal("text-lg font-black text-base-content")
    val logoCopy   = Css.literal("hidden sm:block")
    val logoTitle  = Css.literal("text-base font-black text-base-content")
    val themeLabel = Css.literal("hidden sm:inline text-xs font-semibold uppercase tracking-[0.24em]")
    val darkOnly   = Css.literal("hidden dark:inline")
    val lightOnly  = Css.literal("dark:hidden")

  object footer:

    val root =
      Daisy.groups.footerBase |+| Css.literal("rounded-[2rem] border border-base-300/60 bg-base-100 px-8 py-10 text-base-content shadow-[0_20px_70px_-35px_rgba(15,23,42,0.45)]")

    val aside       = Css.literal("space-y-3")
    val nav         = Css.literal("grid gap-3 text-sm font-medium sm:grid-flow-col sm:gap-6")
    val caption     = Css.literal("text-sm text-base-content/55")
    val eyebrow     = Css.literal("text-xs font-semibold uppercase tracking-[0.28em] text-primary")
    val title       = Css.literal("text-2xl font-black")
    val description = Css.literal("max-w-2xl text-sm leading-7 text-base-content/65")

  object form:

    val grid        = Css.literal("grid gap-5")
    val composeGrid = Css.literal("grid gap-6")
    val scaffold    = Css.literal("grid flex-1 gap-6 lg:grid-cols-[0.9fr_1.1fr]")

    val marketing =
      Css.literal("flex flex-col justify-between rounded-[2.5rem] border border-primary/15 bg-gradient-to-br from-secondary via-neutral to-primary px-8 py-10 text-primary-content shadow-[0_30px_90px_-40px_rgba(15,23,42,0.85)] sm:px-10 sm:py-12")

    val marketingText     = Css.literal("space-y-5")
    val marketingEyebrow  = Css.literal("text-xs font-semibold uppercase tracking-[0.28em] text-white/70")
    val marketingTitle    = Css.literal("text-4xl font-black leading-none sm:text-5xl")
    val marketingSubtitle = Css.literal("max-w-xl text-base leading-8 text-white/78")
    val marketingStats    = Css.literal("grid gap-4 sm:grid-cols-2")
    val statCard          = Css.literal("rounded-[1.75rem] border border-white/10 bg-white/8 px-5 py-5 backdrop-blur")
    val statTitle         = Css.literal("text-xs font-semibold uppercase tracking-[0.24em] text-white/70")
    val statDescription   = Css.literal("mt-2 text-sm leading-7 text-white/80")
    val fieldset          = Daisy.groups.fieldsetBase |+| Css.literal("gap-4")
    val compactFieldset   = Daisy.groups.fieldsetBase |+| Css.literal("gap-3")

    val fieldLabel = Daisy.groups.fieldsetLabelBase |+| Css.literal(
      "text-sm font-semibold uppercase tracking-[0.18em] text-base-content/70"
    )

    val fieldHint    = Daisy.groups.fieldsetLabelBase |+| Css.literal("text-sm text-base-content/55")
    val requiredMark = Css.literal("text-primary")

    val fileLabel =
      Css.literal("flex cursor-pointer items-center justify-between gap-4 rounded-[1.5rem] border border-base-300/60 bg-base-100 px-4 py-4 shadow-sm")

    val fileCopy        = Css.literal("space-y-1")
    val fileTitle       = Css.literal("font-semibold text-base-content")
    val fileDescription = Css.literal("text-sm text-base-content/60")
    val fileText        = Css.literal("space-y-1")
    val fileInput       = Daisy.groups.fileInputBase |+| Css.literal("w-full max-w-xs")
    val previewFrame    = Css.literal("overflow-hidden rounded-[1.5rem] border border-base-300/60 bg-base-200 p-4")
    val previewImage    = Css.literal("h-24 w-24 rounded-2xl object-cover")

  object jobs:

    val toolbar =
      Css.literal("flex flex-col gap-3 rounded-[2rem] border border-base-300/60 bg-base-100 px-5 py-5 shadow-[0_20px_70px_-35px_rgba(15,23,42,0.45)] sm:flex-row sm:items-end sm:justify-between")

    val statPill =
      Css.literal("rounded-[1.5rem] border border-base-300/60 bg-base-200/70 px-4 py-3 text-sm font-semibold text-base-content/70")

    val cardBody         = Css.literal("gap-6 p-5 sm:p-6")
    val clickableCard    = Css.literal("job-card-clickable cursor-pointer")
    val cardLayout       = Css.literal("flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between")
    val cardLayoutDetail = Css.literal("flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between")
    val previewRow       = Css.literal("flex min-w-0 gap-4")
    val previewRowLarge  = Css.literal("flex min-w-0 gap-5")
    val avatarWrap       = Css.of(Daisy.atoms.avatar)
    val avatarFrame      = Css.literal("w-16 rounded-[1.5rem] border border-base-300/70 bg-base-200 p-2 shadow-sm")
    val avatarFrameLarge = Css.literal("w-24 rounded-[2rem] border border-base-300/70 bg-base-200 p-3 shadow-sm")
    val avatarImage      = Css.literal("rounded-[1rem] object-cover")
    val avatarImageLarge = Css.literal("rounded-[1.5rem] object-cover")
    val copyColumn       = Css.literal("min-w-0 space-y-3")
    val copyColumnLarge  = Css.literal("min-w-0 space-y-4")
    val heading          = Css.literal("space-y-1")
    val company          = Css.literal("text-xs font-semibold uppercase tracking-[0.24em] text-primary")
    val companyWide      = Css.literal("text-xs font-semibold uppercase tracking-[0.28em] text-primary")
    val title            = Css.literal("text-2xl font-black leading-tight text-base-content")
    val detailTitle      = Css.literal("text-3xl font-black leading-tight text-base-content sm:text-4xl")
    val titleLink        = Css.literal("transition hover:text-primary")
    val metaRow          = Css.literal("flex flex-wrap gap-2")
    val actions          = Css.literal("flex shrink-0 flex-wrap gap-3")
    val actionsStack     = Css.literal("flex shrink-0 flex-col items-start gap-3 lg:items-end")
    val actionHint       = Css.literal("text-sm font-semibold uppercase tracking-[0.2em] text-primary")
    val routeValue       = Css.literal("job-card-route max-h-0 overflow-hidden text-[0px] leading-none opacity-0")
    val description      = Css.literal("line-clamp-3 text-sm leading-7 text-base-content/68")
    val detailTime       = Css.literal("text-sm font-semibold uppercase tracking-[0.2em] text-base-content/55")

    val detailPill =
      Css.literal("rounded-full border border-base-300/70 bg-base-200/70 px-3 py-2 text-xs font-semibold uppercase tracking-[0.16em] text-base-content/70")

  object filter:

    val header       = Css.literal("space-y-3")
    val intro        = Css.literal("text-sm leading-7 text-base-content/65")
    val actionGrid   = Css.literal("grid gap-3 sm:grid-cols-2")
    val groupContent = Css.literal("grid gap-3 max-h-80 overflow-auto pr-1")

    val collapse =
      Daisy.groups.collapseBase |+| Css.literal("rounded-[1.75rem] border border-base-300/60 bg-base-100")

    val collapseTitle =
      Css.of(Daisy.atoms.collapseTitle) |+| Css.literal("text-base font-black text-base-content")

    val collapseBody = Css.of(Daisy.atoms.collapseContent) |+| Css.literal("pt-1")

  object post:

    val rail               = Css.literal("space-y-6")
    val previewHeader      = Css.literal("space-y-2")
    val previewEyebrow     = Css.literal("text-xs font-semibold uppercase tracking-[0.28em] text-primary")
    val previewTitle       = Css.literal("text-2xl font-black")
    val previewCard        = Css.literal("rounded-[2rem] border border-base-300/60 bg-base-200/60 p-5 shadow-inner")
    val previewTop         = Css.literal("flex items-start gap-4")
    val previewCopy        = Css.literal("min-w-0 space-y-2")
    val previewDescription = Css.literal("mt-5 text-sm leading-7 text-base-content/70")
    val previewTags        = Css.literal("mt-5 flex flex-wrap gap-2")
    val applyBox           = Css.literal("mt-5 rounded-[1.5rem] border border-base-300/60 bg-base-100 px-4 py-4")
    val applyEyebrow       = Css.literal("text-xs font-semibold uppercase tracking-[0.24em] text-base-content/50")
    val applyValue         = Css.literal("mt-2 truncate text-sm font-semibold text-base-content/70")
    val checklist          = Css.literal("rounded-[2rem] border border-base-300/60 bg-base-100 px-5 py-5")
    val checklistTitle     = Css.literal("text-xs font-semibold uppercase tracking-[0.24em] text-primary")
    val checklistList      = Css.literal("mt-4 grid gap-3 text-sm leading-7 text-base-content/70")

    val checklistItem =
      Css.literal("flex items-center justify-between gap-3 rounded-[1.25rem] border border-base-300/50 bg-base-200/50 px-4 py-3")

  object markdown:

    val prose =
      Css.literal("rounded-[2rem] border border-base-300/60 bg-base-200/45 px-5 py-6 text-base-content/80 shadow-inner [&_a]:link [&_a]:link-primary [&_blockquote]:border-l-4 [&_blockquote]:border-primary [&_blockquote]:pl-4 [&_code]:rounded-md [&_code]:bg-base-300 [&_code]:px-1.5 [&_code]:py-1 [&_h1]:text-3xl [&_h1]:font-black [&_h2]:mt-8 [&_h2]:text-2xl [&_h2]:font-black [&_h3]:mt-6 [&_h3]:text-xl [&_h3]:font-bold [&_li]:my-2 [&_ol]:ml-6 [&_ol]:list-decimal [&_p]:my-4 [&_pre]:overflow-x-auto [&_pre]:rounded-2xl [&_pre]:bg-neutral [&_pre]:p-4 [&_pre]:text-neutral-content [&_ul]:ml-6 [&_ul]:list-disc")

  object notFound:

    val title       = Css.literal("text-4xl font-black")
    val description = Css.literal("max-w-xl text-base leading-7 text-base-content/65")

  object button:

    val ghostSurface = Css.literal("border border-base-300/70 bg-base-100/70")
    val ghostSolid   = Css.literal("border border-base-300/70 bg-base-100")

    val ghostSolidButton = Daisy.groups.buttonBase |+| Daisy.tone.buttonGhost |+| ghostSolid |+| Css.literal(
      "font-semibold shadow-sm transition-transform duration-200 hover:-translate-y-0.5"
    )

    val primaryLink = Daisy.groups.buttonBase |+| Daisy.tone.buttonPrimary |+| Css.literal(
      "font-semibold shadow-sm transition-transform duration-200 hover:-translate-y-0.5"
    )

  object alert:

    val warningCard = Daisy.groups.alertBase |+| Css.of(Daisy.atoms.alertWarning) |+| Css.literal(
      "max-w-xl rounded-[1.75rem] shadow-sm"
    )

  object icon:

    val tokenWrap =
      Css.literal("inline-flex min-w-10 items-center justify-center rounded-full bg-base-200 px-2 py-1 text-[0.65rem] font-black uppercase tracking-[0.2em]")

    val small   = Css.literal("size-4")
    val regular = Css.literal("size-5")

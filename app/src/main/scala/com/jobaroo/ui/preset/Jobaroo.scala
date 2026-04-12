package com.jobaroo.ui.preset

import cats.syntax.semigroup.*
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.syntax.all.*

object Jobaroo:

  object shell:

    val root = Css.literal("min-h-screen bg-base-200 text-base-content")

    val inner =
      Css.literal("mx-auto flex min-h-screen w-full max-w-[1560px] flex-col gap-5 px-4 py-4 sm:px-6 lg:px-8")

    val main      = Css.literal("flex-1")
    val page      = Css.literal("flex flex-1 flex-col gap-5 pb-8")
    val split     = Css.literal("grid gap-5 xl:grid-cols-[320px_minmax(0,1fr)] xl:items-start")
    val splitWide = Css.literal("grid gap-5 xl:grid-cols-[minmax(0,1fr)_390px] xl:items-start")
    val stack     = Css.literal("space-y-5")
    val gridGap5  = Css.literal("grid gap-4 lg:gap-5")

  object state:

    val centered      = Css.literal("grid place-items-center py-16 sm:py-24")
    val centeredShort = Css.literal("grid place-items-center py-10")
    val centeredTight = Css.literal("grid place-items-center py-6")
    val centeredWide  = Css.literal("grid place-items-center py-20")

  object hero:

    val root =
      Daisy.groups.heroBase |+| Css.literal(
        "overflow-hidden rounded-[2rem] border border-neutral/70 bg-gradient-to-br from-neutral via-[#14120d] to-[#271c0c] text-neutral-content shadow-[0_34px_90px_-52px_rgba(0,0,0,0.82)]"
      )

    val content   = Daisy.groups.heroContentBase |+| Css.literal("w-full max-w-none px-6 py-8 sm:px-8 sm:py-10")
    val grid      = Css.literal("grid w-full gap-6 lg:grid-cols-[minmax(0,1fr)_auto] lg:items-end")
    val textStack = Css.literal("space-y-4")

    val eyebrow = Daisy.groups.badgeBase |+| Css.of(Daisy.atoms.badgeOutline) |+| Css.literal(
      "border-primary/35 bg-primary/10 px-3 py-2 text-[0.68rem] font-bold uppercase tracking-[0.28em] text-primary"
    )

    val title    = Css.literal("max-w-4xl text-4xl font-black leading-[0.95] tracking-[-0.05em] sm:text-5xl lg:text-[4.45rem]")
    val subtitle = Css.literal("max-w-3xl text-sm leading-7 text-neutral-content/72 sm:text-base")
    val actions  = Css.literal("flex flex-wrap items-center gap-2 lg:justify-end")

    val counterBadge = Daisy.groups.badgeBase |+| Css.literal(
      "rounded-full border border-white/12 bg-white/6 px-3 py-2 text-[0.68rem] font-bold uppercase tracking-[0.22em] text-neutral-content"
    )

  object section:

    val wrap     = Css.literal("space-y-3")
    val eyebrow  = Css.literal("text-[0.7rem] font-bold uppercase tracking-[0.28em] text-primary")
    val title    = Css.literal("text-3xl font-black leading-tight tracking-[-0.045em] text-base-content sm:text-4xl")
    val subtitle = Css.literal("max-w-2xl text-sm leading-7 text-base-content/66 sm:text-base")

  object surface:

    val card = Daisy.groups.cardBase |+| Css.literal(
      "rounded-[1.75rem] border border-base-300 bg-base-100 shadow-[0_18px_42px_-28px_rgba(17,17,17,0.24)]"
    )

    val body            = Daisy.groups.cardBodyBase |+| Css.literal("gap-5")
    val bodyPanel       = Daisy.groups.cardBodyBase |+| Css.literal("gap-5 p-5")
    val bodySpacious    = Daisy.groups.cardBodyBase |+| Css.literal("gap-7 p-5 sm:p-7 lg:p-8")
    val bodyComfortable = Daisy.groups.cardBodyBase |+| Css.literal("gap-7 p-5 sm:p-6 lg:p-7")
    val bodyCompact     = Daisy.groups.cardBodyBase |+| Css.literal("gap-5 p-5 sm:p-6")

    val interactive = Css.literal(
      "overflow-hidden transition duration-200 hover:-translate-y-0.5 hover:border-neutral/60 hover:shadow-[0_24px_56px_-30px_rgba(17,17,17,0.28)]"
    )

    val innerSoft  = Css.literal("rounded-[1.5rem] border border-base-300 bg-base-200/85 p-4")
    val boxSoft    = Css.literal("rounded-[1.3rem] border border-base-300 bg-base-100 px-4 py-4")
    val stickyRail = Css.literal("sticky top-24")
    val fullHeight = Css.literal("h-full")

  object nav:

    val navbar =
      Daisy.groups.navbarBase |+| Css.literal(
        "rounded-[1.65rem] border border-neutral/75 bg-neutral px-4 py-3 text-neutral-content shadow-[0_24px_66px_-42px_rgba(0,0,0,0.9)]"
      )

    val sticky      = Css.literal("sticky top-0 z-40")
    val start       = Css.literal("navbar-start gap-3")
    val center      = Css.literal("navbar-center hidden xl:flex")
    val end         = Css.literal("navbar-end flex items-center gap-2")
    val desktopCopy = Css.literal("hidden lg:block")
    val menu        = Daisy.groups.menuBase |+| Css.literal("gap-1 bg-white/5 p-1")

    val link =
      Css.literal("rounded-xl px-4 py-2 text-sm font-semibold text-neutral-content/74 transition hover:bg-white/10 hover:text-primary")

    val auxLink = Daisy.groups.linkBase |+| Css.literal("font-semibold text-primary no-underline hover:text-primary/80")

    val logo =
      Css.literal("group inline-flex items-center gap-3 rounded-[1.2rem] border border-white/10 bg-white/5 px-3 py-2 transition hover:border-primary/35 hover:bg-white/8")

    val logoImage  = Css.literal("h-10 w-10 rounded-[1rem] bg-white object-cover p-1")
    val themeBtn   = Css.literal("btn btn-ghost rounded-xl border border-white/10 bg-white/5 px-3 text-neutral-content hover:bg-white/10 hover:text-primary")
    val sessionRow = Css.literal("hidden sm:flex items-center gap-2")
    val subtitle   = Css.literal("text-[0.66rem] font-bold uppercase tracking-[0.24em] text-primary/80")
    val title      = Css.literal("text-base font-black tracking-tight text-white")
    val logoCopy   = Css.literal("hidden sm:block")
    val logoTitle  = Css.literal("text-sm font-bold tracking-tight text-white")
    val themeLabel = Css.literal("hidden sm:inline text-[0.68rem] font-semibold uppercase tracking-[0.22em]")
    val darkOnly   = Css.literal("hidden dark:inline-flex")
    val lightOnly  = Css.literal("dark:hidden")

  object footer:

    val root =
      Daisy.groups.footerBase |+| Css.literal(
        "grid gap-8 rounded-[1.75rem] border border-neutral/75 bg-neutral px-6 py-8 text-left text-neutral-content shadow-[0_28px_76px_-42px_rgba(0,0,0,0.85)] lg:grid-cols-[1.2fr_auto] lg:items-start"
      )

    val aside       = Css.literal("space-y-3")
    val nav         = Css.literal("grid gap-3 text-sm font-semibold sm:grid-flow-col sm:gap-5 lg:self-center")
    val caption     = Css.literal("text-xs font-semibold uppercase tracking-[0.22em] text-neutral-content/46 lg:col-span-2")
    val eyebrow     = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.28em] text-primary")
    val title       = Css.literal("max-w-xl text-2xl font-black tracking-tight text-white")
    val description = Css.literal("max-w-2xl text-sm leading-7 text-neutral-content/66")

  object form:

    val grid        = Css.literal("grid gap-5")
    val composeGrid = Css.literal("grid gap-5")
    val scaffold    = Css.literal("grid flex-1 gap-5 lg:grid-cols-[0.88fr_1.12fr]")

    val marketing =
      Css.literal(
        "flex flex-col justify-between rounded-[2rem] border border-neutral/70 bg-gradient-to-br from-neutral via-[#15120d] to-[#271c0c] px-7 py-8 text-neutral-content shadow-[0_34px_90px_-52px_rgba(0,0,0,0.82)] sm:px-8 sm:py-10"
      )

    val marketingText     = Css.literal("space-y-4")
    val marketingEyebrow  = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.28em] text-primary/82")
    val marketingTitle    = Css.literal("text-4xl font-black leading-[0.95] tracking-[-0.05em] sm:text-5xl")
    val marketingSubtitle = Css.literal("max-w-xl text-sm leading-7 text-neutral-content/72 sm:text-base")
    val marketingStats    = Css.literal("grid gap-3 sm:grid-cols-2")
    val statCard          = Css.literal("rounded-[1.35rem] border border-white/10 bg-white/6 px-4 py-4")
    val statTitle         = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.24em] text-primary/80")
    val statDescription   = Css.literal("mt-2 text-sm leading-6 text-neutral-content/76")
    val fieldset          = Daisy.groups.fieldsetBase |+| Css.literal("gap-4")
    val compactFieldset   = Daisy.groups.fieldsetBase |+| Css.literal("gap-2.5")

    val fieldLabel = Daisy.groups.fieldsetLabelBase |+| Css.literal(
      "items-center justify-between gap-3 text-[0.72rem] font-bold uppercase tracking-[0.2em] text-base-content/72"
    )

    val fieldHint    = Daisy.groups.fieldsetLabelBase |+| Css.literal("text-sm leading-6 text-base-content/56")
    val requiredMark = Css.literal("text-primary")

    val fileLabel =
      Css.literal(
        "flex cursor-pointer items-center justify-between gap-4 rounded-[1.25rem] border border-base-300 bg-base-200/70 px-4 py-4 transition hover:border-neutral/45"
      )

    val fileCopy        = Css.literal("space-y-1")
    val fileTitle       = Css.literal("font-semibold text-base-content")
    val fileDescription = Css.literal("text-sm leading-6 text-base-content/58")
    val fileText        = Css.literal("space-y-1")
    val fileInput       = Daisy.groups.fileInputBase |+| Css.literal("w-full max-w-xs rounded-[1rem]")
    val previewFrame    = Css.literal("overflow-hidden rounded-[1.3rem] border border-base-300 bg-base-200/90 p-3")
    val previewImage    = Css.literal("h-24 w-24 rounded-[1rem] object-cover")

  object jobs:

    val toolbar =
      Css.literal(
        "flex flex-col gap-4 rounded-[1.6rem] border border-base-300 bg-base-100 px-5 py-5 shadow-[0_18px_42px_-28px_rgba(17,17,17,0.22)] sm:flex-row sm:items-end sm:justify-between"
      )

    val statPill =
      Css.literal("rounded-full border border-primary/25 bg-primary/12 px-4 py-2 text-[0.72rem] font-bold uppercase tracking-[0.18em] text-base-content")

    val cardBody         = Css.literal("gap-5 p-5 sm:p-6")
    val clickableCard    = Css.literal("job-card-clickable cursor-pointer")
    val cardLayout       = Css.literal("flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between")
    val cardLayoutDetail = Css.literal("flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between")
    val previewRow       = Css.literal("flex min-w-0 items-start gap-4")
    val previewRowLarge  = Css.literal("flex min-w-0 items-start gap-5")
    val avatarWrap       = Css.of(Daisy.atoms.avatar)
    val avatarFrame      = Css.literal("w-16 rounded-[1.25rem] border border-base-300 bg-base-200 p-2")
    val avatarFrameLarge = Css.literal("w-24 rounded-[1.5rem] border border-base-300 bg-base-200 p-3")
    val avatarImage      = Css.literal("rounded-[0.9rem] object-cover")
    val avatarImageLarge = Css.literal("rounded-[1.2rem] object-cover")
    val copyColumn       = Css.literal("min-w-0 space-y-3")
    val copyColumnLarge  = Css.literal("min-w-0 space-y-4")
    val heading          = Css.literal("space-y-2")
    val company          = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.24em] text-primary")
    val companyWide      = Css.literal("text-[0.72rem] font-bold uppercase tracking-[0.28em] text-primary")
    val title            = Css.literal("text-[1.6rem] font-black leading-[1.02] tracking-[-0.045em] text-base-content")
    val detailTitle      = Css.literal("text-3xl font-black leading-[1] tracking-[-0.05em] text-base-content sm:text-[3.5rem]")
    val titleLink        = Css.literal("transition hover:text-primary")
    val metaRow          = Css.literal("flex flex-wrap gap-2")
    val actions          = Css.literal("flex shrink-0 flex-wrap gap-3")
    val actionsStack     = Css.literal("flex shrink-0 flex-col items-start gap-3 lg:items-end")
    val actionHint       = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.22em] text-base-content/48")
    val routeValue       = Css.literal("job-card-route max-h-0 overflow-hidden text-[0px] leading-none opacity-0")
    val description      = Css.literal("line-clamp-3 text-sm leading-7 text-base-content/68")
    val detailTime       = Css.literal("text-[0.7rem] font-bold uppercase tracking-[0.22em] text-base-content/48")

    val detailPill =
      Css.literal(
        "inline-flex items-center gap-2 rounded-full border border-base-300 bg-base-200 px-3 py-2 text-[0.72rem] font-semibold uppercase tracking-[0.14em] text-base-content/76"
      )

  object filter:

    val header       = Css.literal("space-y-3")
    val intro        = Css.literal("text-sm leading-7 text-base-content/64")
    val actionGrid   = Css.literal("grid gap-3")
    val groupContent = Css.literal("grid gap-2.5 max-h-80 overflow-auto pr-1")

    val collapse =
      Daisy.groups.collapseBase |+| Css.literal("rounded-[1.35rem] border border-base-300 bg-base-100 shadow-none")

    val collapseTitle =
      Css.of(Daisy.atoms.collapseTitle) |+| Css.literal("pr-12 text-sm font-black tracking-tight text-base-content")

    val collapseBody = Css.of(Daisy.atoms.collapseContent) |+| Css.literal("pt-0")

  object post:

    val rail               = Css.literal("space-y-5")
    val previewHeader      = Css.literal("space-y-2")
    val previewEyebrow     = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.28em] text-primary")
    val previewTitle       = Css.literal("text-2xl font-black tracking-tight")
    val previewCard        = Css.literal("rounded-[1.5rem] border border-base-300 bg-base-200/80 p-4")
    val previewTop         = Css.literal("flex items-start gap-4")
    val previewCopy        = Css.literal("min-w-0 space-y-2")
    val previewDescription = Css.literal("mt-5 text-sm leading-7 text-base-content/70")
    val previewTags        = Css.literal("mt-5 flex flex-wrap gap-2")
    val applyBox           = Css.literal("mt-5 rounded-[1.25rem] border border-base-300 bg-base-100 px-4 py-4")
    val applyEyebrow       = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.22em] text-base-content/48")
    val applyValue         = Css.literal("mt-2 truncate text-sm font-semibold text-base-content/74")
    val checklist          = Css.literal("rounded-[1.5rem] border border-base-300 bg-base-100 px-5 py-5")
    val checklistTitle     = Css.literal("text-[0.68rem] font-bold uppercase tracking-[0.24em] text-primary")
    val checklistList      = Css.literal("mt-4 grid gap-3 text-sm leading-7 text-base-content/70")

    val checklistItem =
      Css.literal("flex items-center justify-between gap-3 rounded-[1.1rem] border border-base-300 bg-base-200/70 px-4 py-3")

  object markdown:

    val prose =
      Css.literal(
        "rounded-[1.6rem] border border-base-300 bg-base-200/60 px-5 py-6 text-base-content/82 shadow-inner [&_a]:link [&_a]:link-primary [&_blockquote]:border-l-4 [&_blockquote]:border-primary [&_blockquote]:pl-4 [&_code]:rounded-md [&_code]:bg-base-300 [&_code]:px-1.5 [&_code]:py-1 [&_h1]:text-3xl [&_h1]:font-black [&_h2]:mt-8 [&_h2]:text-2xl [&_h2]:font-black [&_h3]:mt-6 [&_h3]:text-xl [&_h3]:font-bold [&_li]:my-2 [&_ol]:ml-6 [&_ol]:list-decimal [&_p]:my-4 [&_pre]:overflow-x-auto [&_pre]:rounded-2xl [&_pre]:bg-neutral [&_pre]:p-4 [&_pre]:text-neutral-content [&_ul]:ml-6 [&_ul]:list-disc"
      )

  object notFound:

    val title       = Css.literal("text-4xl font-black tracking-tight")
    val description = Css.literal("max-w-xl text-sm leading-7 text-base-content/66 sm:text-base")

  object button:

    val ghostSurface = Css.literal("border border-base-300 bg-base-100")
    val ghostSolid   = Css.literal("border border-base-300 bg-base-100")

    val ghostSolidButton = Daisy.groups.buttonBase |+| Daisy.tone.buttonGhost |+| ghostSolid |+| Css.literal(
      "rounded-[1.1rem] font-semibold shadow-none transition duration-200 hover:-translate-y-0.5"
    )

    val primaryLink = Daisy.groups.buttonBase |+| Daisy.tone.buttonPrimary |+| Css.literal(
      "rounded-[1.1rem] font-semibold shadow-none transition duration-200 hover:-translate-y-0.5"
    )

  object alert:

    val warningCard = Daisy.groups.alertBase |+| Css.of(Daisy.atoms.alertWarning) |+| Css.literal(
      "max-w-xl rounded-[1.35rem] border border-primary/25 bg-primary/12 px-5 py-4 text-base-content shadow-none"
    )

  object icon:

    val tokenWrap = Css.literal("inline-flex shrink-0 items-center justify-center text-current")
    val small     = Css.literal("size-4")
    val regular   = Css.literal("size-5")

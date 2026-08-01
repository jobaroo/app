package com.jobaroo.ui.preset

import cats.syntax.semigroup.*
import com.jobaroo.ui.core.Css
import com.jobaroo.ui.syntax.all.*

object Jobaroo:

  object shell:

    val root         = Css.literal("min-h-screen bg-base-100 text-base-content")
    val inner        = Css.literal("flex min-h-screen flex-col")
    val main         = Css.literal("flex-1")
    val page         = Css.literal("flex flex-1 flex-col")
    val contentWidth = Css.literal("mx-auto w-full max-w-[1400px] px-4")
    val narrowWidth  = Css.literal("mx-auto w-full max-w-5xl px-4")
    val split        = Css.literal("grid gap-6 lg:grid-cols-[19rem_minmax(0,1fr)] xl:grid-cols-[20rem_minmax(0,1fr)]")
    val splitWide    = Css.literal("grid gap-6 lg:grid-cols-[minmax(0,1fr)_22rem]")
    val stack        = Css.literal("space-y-6")
    val gridGap5     = Css.literal("grid grid-cols-1 gap-5 xl:grid-cols-2 2xl:grid-cols-3")

  object state:

    val centered      = Css.literal("grid place-items-center py-20")
    val centeredShort = Css.literal("grid place-items-center py-12")
    val centeredTight = Css.literal("grid place-items-center py-8")
    val centeredWide  = Css.literal("grid place-items-center py-24")

  object hero:

    val root =
      Daisy.groups.heroBase |+| Css.literal("w-full bg-black text-white")

    val content =
      Daisy.groups.heroContentBase |+| Css.literal("mx-auto w-full max-w-6xl px-4 py-10 lg:px-8 lg:py-14")

    val grid      = Css.literal("grid gap-6")
    val textStack = Css.literal("space-y-3 text-center")

    val eyebrow =
      Daisy.groups.badgeBase |+| Css.literal("border-none bg-primary/15 text-primary px-3 py-2 text-xs font-semibold uppercase tracking-wide")

    val title    = Css.literal("text-3xl font-bold leading-tight lg:text-4xl")
    val subtitle = Css.literal("mx-auto max-w-2xl text-base text-white/70 lg:text-lg")
    val actions  = Css.literal("flex flex-wrap justify-center gap-3")

    val counterBadge =
      Daisy.groups.badgeBase |+| Css.literal("border-none bg-white/10 px-3 py-2 text-xs font-medium text-white")

    val statsRow   = Css.literal("flex flex-wrap justify-center gap-8 lg:gap-16")
    val statCard   = Css.literal("text-center")
    val statValue  = Css.literal("text-3xl font-bold text-primary lg:text-4xl")
    val statLabel  = Css.literal("text-sm text-white/60")

  object section:

    val wrap     = Css.literal("space-y-2")
    val eyebrow  = Css.literal("text-xs font-semibold uppercase tracking-wide text-primary")
    val title    = Css.literal("text-2xl font-semibold lg:text-3xl")
    val subtitle = Css.literal("text-sm text-base-content/60")
    val backLink = Css.literal(
      "inline-flex items-center gap-2 rounded-md px-2 py-1 text-sm font-medium text-base-content/60 transition hover:bg-base-200 hover:text-base-content"
    )
    val backLinkInverse = Css.literal(
      "inline-flex items-center gap-2 rounded-md px-2 py-1 text-sm font-medium text-white/70 transition hover:bg-white/10 hover:text-white"
    )

  object surface:

    val card = Daisy.groups.cardBase |+| Css.literal(
      "rounded-lg border-2 border-[#d0d0d0] bg-base-100 shadow-sm dark:border-[#404040]"
    )

    val body            = Daisy.groups.cardBodyBase |+| Css.literal("gap-4 p-5")
    val bodyPanel       = Daisy.groups.cardBodyBase |+| Css.literal("gap-4 p-5")
    val bodySpacious    = Daisy.groups.cardBodyBase |+| Css.literal("gap-5 p-6 lg:p-8")
    val bodyComfortable = Daisy.groups.cardBodyBase |+| Css.literal("gap-5 p-6")
    val bodyCompact     = Daisy.groups.cardBodyBase |+| Css.literal("gap-4 p-5")

    val interactive =
      Css.literal("transition hover:border-primary hover:shadow-[0_4px_12px_rgba(0,0,0,0.1)]")

    val innerSoft  = Css.literal("rounded-lg bg-base-200 p-4")
    val boxSoft    = Css.literal("rounded-lg border border-base-300 bg-base-100 p-4")
    val stickyRail = Css.literal("lg:sticky lg:top-24")
    val fullHeight = Css.literal("h-full")

  object nav:

    val outer  = Css.literal("sticky top-0 z-50 bg-black text-white")
    val navbar = Css.literal("mx-auto flex w-full max-w-6xl items-center justify-between gap-3 px-4 py-3")

    val sticky      = Css.empty
    val start       = Css.literal("flex items-center gap-3")
    val center      = Css.literal("hidden md:flex items-center gap-2")
    val end         = Css.literal("flex items-center gap-2")
    val desktopCopy = Css.literal("hidden sm:block")
    val menu        = Css.literal("hidden md:flex items-center gap-2")

    val link =
      Css.literal("btn btn-ghost btn-sm border-none text-white font-medium normal-case hover:bg-white/10")

    val auxLink =
      Daisy.groups.linkBase |+| Css.literal("text-primary no-underline hover:text-primary")

    val logo = Css.literal("flex items-center gap-3")
    val logoImage = Css.literal("h-11 w-11 object-contain")
    val themeBtn  = Css.literal("btn btn-circle btn-ghost btn-sm border-none text-white hover:bg-white/10")
    val sessionRow = Css.literal("hidden sm:flex items-center gap-2")
    val subtitle   = Css.literal("text-xs font-medium text-primary")
    val title      = Css.literal("hidden text-2xl font-bold sm:inline")
    val logoCopy   = Css.literal("hidden sm:block")
    val logoTitle  = Css.literal("jobaroo-brand text-2xl font-bold")
    val themeLabel = Css.literal("sr-only")
    val darkOnly   = Css.literal("hidden dark:inline-flex")
    val lightOnly  = Css.literal("dark:hidden")

    val mobileMenu       = Css.literal("dropdown dropdown-end md:hidden")
    val mobileMenuButton = Css.literal("btn btn-ghost btn-circle btn-sm border-none text-white hover:bg-white/10")
    val mobileDropdown =
      Css.literal("menu dropdown-content z-50 mt-2 w-52 rounded-box bg-base-100 p-2 text-base-content shadow-xl")

  object footer:

    val outer = Css.literal("mt-12 bg-black text-white")

    val root =
      Daisy.groups.footerBase |+| Css.literal(
        "mx-auto grid w-full max-w-[1400px] gap-8 px-4 py-10 text-left sm:grid-cols-2 lg:grid-cols-[1.2fr_repeat(2,minmax(0,1fr))]"
      )

    val aside       = Css.literal("space-y-4")
    val nav         = Css.literal("space-y-2")
    val caption     = Css.literal("border-t border-white/20 px-4 py-4 text-center text-sm text-white/70")
    val eyebrow     = Css.literal("text-xs font-semibold uppercase tracking-wide text-primary")
    val title       = Css.literal("jobaroo-brand text-base font-semibold")
    val description = Css.literal("text-sm text-white/70")
    val logo        = Css.literal("h-12 w-12 object-contain")
    val link        = Css.literal("link link-hover text-sm text-white no-underline")
    val columnTitle = Css.literal("footer-title text-white/80")

  object form:

    val page        = Css.literal("mx-auto w-full max-w-5xl px-4 py-8 lg:py-12")
    val grid        = Css.literal("grid gap-5")
    val composeGrid = Css.literal("grid gap-5")
    val scaffold    = Css.literal("space-y-6")
    val backRow     = Css.literal("mb-4")

    val marketing = Css.literal(
      "rounded-lg bg-black px-6 py-8 text-white shadow-lg lg:px-8 lg:py-10"
    )

    val marketingText     = Css.literal("space-y-3")
    val marketingEyebrow  = Css.literal("text-xs font-semibold uppercase tracking-wide text-primary")
    val marketingTitle    = Css.literal("text-3xl font-bold leading-tight lg:text-4xl")
    val marketingSubtitle = Css.literal("max-w-2xl text-base text-white/70")
    val marketingStats    = Css.literal("grid gap-4 sm:grid-cols-2")
    val statCard          = Css.literal("rounded-lg bg-white/5 p-4")
    val statTitle         = Css.literal("text-2xl font-bold text-primary")
    val statDescription   = Css.literal("mt-1 text-sm text-white/70")

    val fieldset        = Daisy.groups.fieldsetBase |+| Css.literal("gap-2")
    val compactFieldset = Daisy.groups.fieldsetBase |+| Css.literal("gap-2")

    val fieldLabel = Daisy.groups.fieldsetLabelBase |+| Css.literal(
      "label mb-1 p-0 text-sm font-medium text-base-content"
    )

    val fieldHint    = Daisy.groups.fieldsetLabelBase |+| Css.literal("p-0 text-xs text-base-content/50")
    val requiredMark = Css.literal("text-error")

    val fileLabel =
      Css.literal("flex items-center justify-between gap-4 rounded-lg border border-dashed border-base-300 bg-base-100 p-4")

    val fileCopy        = Css.literal("space-y-1")
    val fileTitle       = Css.literal("text-sm font-medium")
    val fileDescription = Css.literal("text-xs text-base-content/50")
    val fileText        = Css.literal("space-y-1")
    val fileInput       = Daisy.groups.fileInputBase |+| Css.literal("file-input-sm")
    val previewFrame    = Css.literal("rounded-lg border border-base-300 bg-base-200 p-3")
    val previewImage    = Css.literal("h-20 w-20 rounded-lg object-contain bg-white p-2")

    val formCard       = Css.literal("rounded-2xl border border-[#d8d2c4] bg-base-100/95 shadow-[0_16px_40px_rgba(0,0,0,0.06)] dark:border-[#404040]")
    val sectionBlock   = Css.literal("space-y-6")
    val sectionDivider = Css.literal("my-8 border-t border-base-300")
    val sectionHeading = Css.literal("mb-1 flex items-center gap-2 text-xl font-semibold")
    val sectionText    = Css.literal("mb-6 text-sm text-base-content/60")
    val submitPanel    = Css.literal("rounded-lg bg-base-200 p-6")
    val submitRow      = Css.literal("mb-4 flex items-center justify-between")
    val submitPrice    = Css.literal("text-2xl font-bold")
    val submitList     = Css.literal("space-y-2 text-sm text-base-content/70")

  object jobs:

    val page         = Css.literal("mx-auto w-full max-w-[1440px] px-4 py-6 lg:px-8")
    val toolbar      = Css.literal("mb-6 flex items-center justify-between gap-3")
    val statPill     = Css.literal("badge badge-outline badge-sm text-xs font-medium")
    val cardBody     = Css.literal("card-body p-5")
    val clickableCard = Css.literal("cursor-pointer block")
    val cardLayout       = Css.literal("flex h-full flex-col gap-3")
    val cardLayoutDetail = Css.literal("grid gap-6 lg:grid-cols-[minmax(0,1fr)_auto]")
    val previewRow       = Css.literal("flex items-start justify-between gap-2")
    val previewRowLarge  = Css.literal("flex items-start gap-4")
    val avatarWrap       = Css.of(Daisy.atoms.avatar)
    val avatarFrame      = Css.literal("h-12 w-12 overflow-hidden rounded-lg bg-base-200")
    val avatarFrameLarge = Css.literal("h-16 w-16 overflow-hidden rounded-lg bg-base-200")
    val avatarImage      = Css.literal("h-full w-full object-contain bg-white p-2")
    val avatarImageLarge = Css.literal("h-full w-full object-contain bg-white p-3")
    val copyColumn       = Css.literal("min-w-0")
    val copyColumnLarge  = Css.literal("min-w-0 space-y-4")
    val heading          = Css.literal("space-y-1")
    val company          = Css.literal("text-sm font-medium text-primary")
    val companyWide      = Css.literal("text-sm font-medium text-primary")
    val title            = Css.literal("text-lg font-semibold leading-snug")
    val detailTitle      = Css.literal("text-3xl font-semibold leading-tight")
    val titleLink        = Css.literal("hover:text-primary transition")
    val metaRow          = Css.literal("flex flex-wrap items-center gap-2")
    val actions          = Css.literal("flex items-center gap-3")
    val actionsStack     = Css.literal("mt-auto flex items-center justify-between gap-3 border-t border-base-200 pt-3")
    val actionHint       = Css.literal("text-xs text-base-content/50")
    val routeValue       = Css.literal("hidden")
    val description      = Css.literal("line-clamp-2 text-sm text-base-content/70")
    val detailTime       = Css.literal("text-sm text-base-content/50")

    val detailPill =
      Css.literal("inline-flex items-center gap-2 rounded-full border border-base-300 bg-base-200 px-3 py-1.5 text-sm text-base-content/80")

    val salary        = Css.literal("text-lg font-bold text-primary")
    val footerRow     = Css.literal("mt-auto flex items-center justify-between border-t border-base-200 pt-3")
    val footerMuted   = Css.literal("text-xs text-base-content/50")
    val footerLink    = Css.literal("text-sm font-medium text-primary")
    val summaryCard   = Css.literal("rounded-2xl border border-[#d8d2c4] bg-base-100/95 shadow-[0_10px_28px_rgba(0,0,0,0.04)] dark:border-[#404040] dark:bg-base-100/90")
    val boardHeader   = Css.literal("rounded-2xl border border-[#eadfba] bg-[#fff8e1] p-6 shadow-[0_10px_28px_rgba(0,0,0,0.03)] dark:border-[#3c3218] dark:bg-[#17120a]")
    val boardHeaderRow = Css.literal("flex items-start justify-between gap-3")
    val boardHeading   = Css.literal("space-y-1")
    val boardTitle     = Css.literal("text-2xl font-semibold tracking-tight")
    val boardSubtitle  = Css.literal("max-w-3xl text-sm text-base-content/60")
    val boardTools     = Css.literal("flex items-center gap-2")
    val topBarButton  = Css.literal("btn btn-outline btn-primary btn-sm font-medium normal-case")
    val sortWrap      = Css.literal("flex items-center gap-2")
    val sortLabel     = Css.literal("text-sm text-base-content/70")
    val toolChip      = Css.literal("inline-flex items-center gap-2 rounded-md border border-base-300 bg-base-100 px-3 py-2 text-xs font-medium text-base-content")

    val detailHero        = Css.literal("bg-black text-white")
    val detailHeroInner   = Css.literal("mx-auto grid w-full max-w-6xl gap-6 px-4 py-10 lg:grid-cols-[minmax(0,1fr)_20rem] lg:items-end")
    val detailLead        = Css.literal("space-y-4")
    val detailBackRow     = Css.literal("mb-2")
    val detailHeroTitle   = Css.literal("text-3xl font-bold leading-tight lg:text-4xl")
    val detailHeroCopy    = Css.literal("max-w-3xl text-sm text-white/70 lg:text-base")
    val detailHeroTags    = Css.literal("flex flex-wrap gap-2")
    val detailHeroActions = Css.literal("space-y-3")
    val detailHeroCard    = Css.literal("rounded-lg border border-white/10 bg-white/5 p-5")
    val detailHeroTime    = Css.literal("text-sm text-white/60")
    val detailGrid        = Css.literal("mx-auto grid w-full max-w-6xl gap-6 px-4 py-8 lg:grid-cols-[minmax(0,1fr)_18rem]")
    val detailMain        = Css.literal("space-y-6")
    val detailRail        = Css.literal("space-y-4 lg:sticky lg:top-24")
    val detailSection     = Css.literal("space-y-4")
    val detailSectionTitle = Css.literal("text-lg font-semibold")
    val detailSectionCopy  = Css.literal("text-sm text-base-content/70")
    val detailList         = Css.literal("space-y-3")
    val detailListItem     = Css.literal("flex items-start gap-2 text-sm text-base-content/80")
    val detailTagRow       = Css.literal("flex flex-wrap gap-1")

  object filter:

    val sidebar = Css.literal("rounded-2xl border border-base-300/80 bg-base-100/95 p-5 shadow-sm lg:sticky lg:top-24 lg:max-h-[calc(100vh-7rem)] lg:overflow-y-auto")
    val header  = Css.literal("mb-5 flex items-center border-b border-base-300 pb-3")
    val intro   = Css.literal("text-sm text-base-content/60")
    val actionGrid = Css.literal("grid gap-2")
    val groupContent = Css.literal("space-y-1")
    val groupWrap    = Css.literal("mb-5 space-y-3")
    val sectionDisabled = Css.literal("opacity-60")
    val searchInput  = Css.literal("input input-bordered input-sm w-full rounded-md border-base-300 bg-base-100 text-sm shadow-none focus:border-primary focus:outline-none")
    val emptyState   = Css.literal("text-xs text-base-content/50")
    val helperText   = Css.literal("text-xs text-base-content/50")

    val collapse =
      Daisy.groups.collapseBase |+| Css.literal("mb-5 rounded-box bg-base-100 border-none")

    val collapseTitle =
      Css.of(Daisy.atoms.collapseTitle) |+| Css.literal("min-h-0 px-0 py-0 pr-0 text-sm font-medium")

    val collapseBody = Css.of(Daisy.atoms.collapseContent) |+| Css.literal("px-0 pt-3 pb-0")

    val sectionTitle = Css.literal("text-sm font-medium")
    val rowOption    = Css.literal("flex items-center gap-2 rounded p-1 hover:bg-base-200")
    val rowOptionDisabled = Css.literal("cursor-not-allowed hover:bg-transparent")
    val pillGroup    = Css.literal("flex flex-wrap gap-1")
    val salaryBox    = Css.literal("rounded-lg bg-base-200 px-3 py-2 text-center text-sm font-semibold text-primary")
    val remoteSwitch =
      Css.literal(
        "inline-flex min-w-16 items-center justify-between rounded-full border border-base-300 bg-base-200 px-1 py-1 transition-colors duration-150 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
      )
    val remoteSwitchOn  = Css.literal("border-primary bg-primary text-primary-content")
    val remoteSwitchOff = Css.literal("text-base-content/70")
    val remoteState     = Css.literal("px-2 text-[10px] font-semibold uppercase tracking-[0.22em]")
    val remoteThumb     = Css.literal("size-5 rounded-full bg-base-100 shadow-sm")

  object post:

    val heroSection = Css.literal("bg-black text-white")
    val heroTitle   = Css.literal("text-3xl font-bold leading-tight lg:text-4xl")
    val heroText    = Css.literal("mx-auto max-w-2xl text-center text-lg text-white/70")
    val heroWrap    = Css.literal("mx-auto max-w-6xl px-4 py-10 text-center lg:py-14")
    val heroBackRow = Css.literal("mb-6 flex justify-start")
    val heroStats   = Css.literal("mt-6 flex flex-wrap justify-center gap-8 lg:gap-16")
    val heroStat    = Css.literal("text-center")
    val heroValue   = Css.literal("text-3xl font-bold text-primary lg:text-4xl")
    val heroLabel   = Css.literal("text-sm text-white/60")

    val page          = Css.literal("mx-auto w-full max-w-6xl px-4 py-8 lg:py-12")
    val rail          = Css.literal("space-y-4 lg:self-start")
    val editorForm    = Css.literal("space-y-8")
    val editorSection = Css.literal("space-y-5")
    val editorHeader  = Css.literal("space-y-1")
    val editorTitleRow = Css.literal("flex items-center gap-2")
    val editorIcon     = Css.literal("text-primary")
    val editorTitle    = Css.literal("text-xl font-semibold")
    val editorCopy     = Css.literal("text-sm text-base-content/60")
    val editorColumns2 = Css.literal("grid gap-4 md:grid-cols-2")
    val editorColumns3 = Css.literal("grid gap-4 md:grid-cols-3")
    val previewHeader = Css.literal("mb-3")
    val previewEyebrow = Css.literal("text-sm font-semibold uppercase tracking-wide text-base-content/60")
    val previewTitle   = Css.literal("text-xl font-semibold")
    val previewCard =
      Css.literal("preview-card card rounded-2xl border border-[#d8d2c4] bg-base-100 shadow-[0_14px_32px_rgba(0,0,0,0.05)] dark:border-[#404040]")
    val previewTop         = Css.literal("flex items-start gap-4")
    val previewCopy        = Css.literal("min-w-0")
    val previewDescription = Css.literal("text-sm text-base-content/70")
    val previewTags        = Css.literal("mt-3 flex flex-wrap gap-1")
    val applyBox           = Css.literal("mt-4 rounded-lg bg-base-200 p-4")
    val applyEyebrow       = Css.literal("text-xs font-semibold uppercase tracking-wide text-base-content/50")
    val applyValue         = Css.literal("mt-2 text-sm text-base-content/70")
    val checklist          = Css.literal("tip-card rounded-2xl border border-base-300 bg-base-100 p-5")
    val checklistTitle     = Css.literal("mb-3 text-base font-semibold")
    val checklistList      = Css.literal("space-y-3 text-sm text-base-content/70")

    val checklistItem =
      Css.literal("flex items-start gap-2")

    val signalsCard = Css.literal("rounded-2xl border border-base-300 bg-base-100 p-5")
    val signalsList = Css.literal("space-y-3")
    val signalsItem = Css.literal("flex items-start gap-2 text-sm text-base-content/80")
    val tipsCard  = Css.literal("rounded-2xl border border-base-300 bg-base-100 p-5")
    val tipsList  = Css.literal("space-y-3")
    val tipsItem  = Css.literal("flex items-start gap-2 text-sm text-base-content/70")
    val supportCard = Css.literal("rounded-lg bg-base-200 p-5")
    val supportText = Css.literal("mb-3 text-sm text-base-content/70")
    val supportBtn  = Css.literal("btn btn-sm btn-ghost w-full normal-case")

  object markdown:

    val prose =
      Css.literal(
        "rounded-lg bg-base-100 text-base-content [&_a]:text-primary [&_a]:underline [&_blockquote]:border-l-4 [&_blockquote]:border-primary [&_blockquote]:pl-4 [&_code]:rounded [&_code]:bg-base-300 [&_code]:px-1.5 [&_code]:py-1 [&_h1]:mb-2 [&_h1]:text-2xl [&_h1]:font-semibold [&_h2]:mb-2 [&_h2]:mt-6 [&_h2]:text-xl [&_h2]:font-semibold [&_h3]:mb-2 [&_h3]:mt-5 [&_h3]:text-lg [&_h3]:font-semibold [&_li]:mb-1 [&_ol]:ml-6 [&_ol]:list-decimal [&_p]:mb-4 [&_pre]:overflow-x-auto [&_pre]:rounded-lg [&_pre]:bg-base-200 [&_pre]:p-4 [&_ul]:ml-6 [&_ul]:list-disc"
      )

  object notFound:

    val title       = Css.literal("text-3xl font-semibold")
    val description = Css.literal("text-base text-base-content/70")

  object button:

    val ghostSurface = Css.literal("border border-white/10 bg-white/10 text-white hover:bg-white/20")
    val ghostSolid   = Css.literal("border border-base-300 bg-base-100 text-base-content hover:bg-base-200")

    val ghostSolidButton =
      Daisy.groups.buttonBase |+| Css.literal("btn btn-ghost btn-sm rounded-md normal-case font-medium")

    val primaryLink =
      Daisy.groups.buttonBase |+| Css.literal("btn btn-primary btn-sm rounded-md normal-case font-medium")

  object alert:

    val warningCard =
      Daisy.groups.alertBase |+| Css.literal("rounded-lg border border-warning/30 bg-warning/10 text-warning-content")

  object icon:

    val tokenWrap = Css.literal("inline-flex shrink-0 items-center justify-center text-current")
    val small     = Css.literal("size-4")
    val regular   = Css.literal("size-5")

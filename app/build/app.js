import '../styles.css'
import { JobarooApp } from '../target/scala-3.2.1/app-opt/main.js'

const savedTheme = window.localStorage.getItem("jobaroo-theme") || "jobaroo-light"
document.documentElement.setAttribute("data-theme", savedTheme)

JobarooApp().launch("app")

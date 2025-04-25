// variables
#let collegeName = "FR. CONCEICAO RODRIGUES COLLEGE OF ENGINEERING"
#let departmentName = "Department of Computer Engineering"

#let class = "T.E. Computer A"
#let subjectName = "Mobile Computing"

#let practicalNo = "7"
#let title = "To develop an android application that creates an alert"
#let dateOfPerformance = "21/04/2025"
#let dateOfSubmission = "25/04/2025"

#let rollNo = "9914"
#let name = "Vivian"

// functions
#let printFile(filename, lang) = {
  set text(font: "JetBrainsMono NF", size: 12pt)

  // Header bar with filename
  rect(
    width: 100%,
    height: 20pt,
    fill: gray,
    inset: 4pt,
    text(
      fill: white,
      "//" + filename
    )
  )

  // Code block with syntax highlighting
  raw(read(filename), lang: lang)
}

#set text(font: "Poppins", size: 12pt)

#set page(
  header: align(center)[
    #heading(level: 1)[#collegeName]
    #heading(level: 2)[#departmentName]
  ]
)

#table(
  align: left, 
  columns:(auto,auto),
  stroke: none,
  [*Class*], [*#class*],
  [*Subject Name*], [*#subjectName*],
)

#rect(width: 100%, height: 10pt,stroke: none)
#table(
  align: left, 
  columns:(auto,1fr),
  inset: 1em,

  [*Practical No.*], [#practicalNo],
  [*Title*], [#title],
  [*Date of Performance*], [#dateOfPerformance],
  [*Date of Submission*], [#dateOfSubmission],
  [*Roll No.*], [#rollNo],
  [*Name*], [#name],
)

#rect(width: 100%, height: 20pt,stroke: none)
*Evaluation:*

#let data = (
  ("Timeline(2)", ""),
  ("Output(3)", ""),
  ("Code Optimization(3)", ""),
  ("Knowledge of the topic(2)", ""),
  ("Total(10)", "")
)

#table(
  columns:(auto, 1fr, auto),
  align: left,
  inset: 1em,

  table.header([*Sr. No*],[*Rubric*], [*Grade*]),

  ..data.enumerate().map(((i, (label, value))) => {
    ([*#str(i + 1)*], [*#label*], value)
  }).flatten()
)

#rect(width: 100%, height: 10pt,stroke: none)
*Signature of the teacher:*



#pagebreak()

== CODE:
#set text(font: "JetBrainsMono NF", size: 12pt)

#printFile("MainActivity.kt", "kt")
#printFile("libs.versions.toml", "toml")
#printFile("build.gradle.kts", "kts")

#pagebreak()
#set text(font: "Poppins", size: 12pt)


== OUTPUT:
#image("./WhatsApp Image 2025-04-25 at 10.23.18 PM.jpeg",
  width: 75%)
#image("WhatsApp Image 2025-04-25 at 10.23.18 PM(1).jpeg",
  width: 75%)
#box(
  image("./WhatsApp Image 2025-04-25 at 10.23.19 PM.jpeg"),
  clip: true,
  inset: (bottom: -50%),
  height: 50%
)
#pagebreak()
#box(
  image("./WhatsApp Image 2025-04-25 at 10.23.19 PM(1).jpeg"),
  clip: true,
  inset: (bottom: -50%),
  height: 47%
)
#box(
  image("./WhatsApp Image 2025-04-25 at 10.23.19 PM(2).jpeg"),
  clip: true,
  inset: (bottom: -50%),
  height: 47%
)

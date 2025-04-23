// variables
#let collegeName = "FR. CONCEICAO RODRIGUES COLLEGE OF ENGINEERING"
#let departmentName = "Department of Computer Engineering"

#let class = "T.E. Computer A"
#let subjectName = "Systems Programming And Compiler Construction"
#let subjectCode = "CPC 601"

#let practicalNo = "6"
#let title = "Macro Processor"
#let dateOfPerformance = "15/04/2025"
#let dateOfSubmission = "23/04/2025"

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
  [*Subject Code*], [*#subjectCode*],
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
  ("Code Optimization(2)", ""),
  ("Postlab(3)", ""),
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

#rect(width: 100%, height: 70pt,stroke: none)
*Signature of the teacher:*



#pagebreak()

== CODE:
#set text(font: "JetBrainsMono NF", size: 12pt)

#printFile("MacroFirstPass.java", "java")
#printFile("MacroSecondPass.java", "java")

#image(
  "./23Apr25_22h13m47s.png",
)

#pagebreak()
#set text(font: "Poppins", size: 12pt)


== INPUT:
#printFile("input.txt", "text")

== OUTPUT:
#image("./23Apr25_21h58m27s.png",
  height: 60%
)
#image("./23Apr25_21h58m41s.png")

#let collegeName = "FR. CONCEICAO RODRIGUES COLLEGE OF ENGINEERING"
#let departmentName = "Department of Computer Engineering"

#set text(font: "Poppins", size: 11pt)

#set page(
  header: align(center)[
    #heading(level: 1)[#collegeName]
    #heading(level: 2)[#departmentName]
  ]
)

#rect(width: 100%, height: 5pt, stroke: none)

== Course , Subject & Experiment Details

#rect(width: 100%, height: 5pt, stroke: none)

#table(
  columns: (1fr, 1fr, 1fr, 1fr),
  align: left,
  inset: 0.7em,

  [*Academic Year*], [*2024-25*],
  [*Estimated Time*], [*03-Hours*],
  [*Course & Semester*],[* T.E. (CMPN)- Sem VI*],
  [*Subject Name & Code*],[*CSS - (CSC602)*],
  [*Module No.*],[*05 – Mapped to CO- 3*],
  [*Chapter Title*],[*Network Security and Applications*],
)

#rect(width: 100%, height: 5pt, stroke: none)
#table(
  columns: (auto, 1fr),
  align: left,
  inset: 1em,

  [*Practical No:*],[*8*],
  [*Title:*],[*DOS attack simulation using Kali Linux*],
  [*Date of performance:*],[24/04/2025],
  [*Date of submission:*],[25/04/2025],
  [*Roll No:*],[*9914*],
  [*Name of the Student:*],[Vivian Vijay Ludrick],
)

#rect(width: 100%, height: 5pt, stroke: none)

#let data = (
  ("On Time completion or submission(2)", ""),
  ("Prepardness(2)", ""),
  ("Skill(4)", ""),
  ("Output(2)", ""),
)

#table(
  columns:(auto, 1fr, auto),
  align: center,
  inset: 1.5em,

  table.header([*Sr. No*],[*Rubric*], [*Grade*]),

  ..data.enumerate().map(((i, (label, value))) => {
    ([*#str(i + 1)*], [*#label*], value)
  }).flatten()
)

#rect(width: 100%, height: 30pt, stroke: none)
*Signature of teacher:*

*Date:*

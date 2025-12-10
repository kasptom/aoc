# Advent of Code – 2025 Day 10: "Factory" part 2. Using Z3 ILP

This page states only the mathematics of the optimization problem.

Given
- A set of indicators (lights) $I$.
- A set of buttons $B$.
- Each press of button $b \in B$ increases by 1 the joltage of every indicator it affects.
- Required joltage $r_i$ for each indicator $i \in I$. 

Variables
- For each button $b \in B$, introduce an integer variable $x_b$ (how many times button $b$ is pressed).

Domain
- $x_b \in {Z},\ x_b \ge 0$ for all $b \in B$.

Parameters
- $A_{i,b} \in \{0,1\}$ indicates whether button $b$ affects indicator $i$ (1 if it does, 0 otherwise).

Constraints (exact target per indicator)
- For every $i \in I$:

  $$
  \sum_{b \in B} A_{i,b} \, x_b = r_i.
  $$

Objective (fewest total presses)

$$
\min \sum_{b \in B} x_b.
$$


## Z3 formula mapping

- Variables $x_b$ (one per button)
-  s $x_0, x_1, \ldots$ stored in `xVars` (one `IntExpr` per button).
- Domain $x_b \ge 0$ (non-negativity)
- For each `x in xVars` add constraint `mkGe(x, 0)`
- Parameters $A_{i,b}$ and $r_i$ 
  - `buttons` $\to$ defines $A_{i,b}$ implicitly: $A_{i,b} = 1$ $\iff$ indicator $i$ is listed in $\text{buttons}[b]$, else $0$.
  - `joltReqs` $\to$ provides $r_i$ for each indicator $i$.
- Constraints $\sum_b A_{i,b} x_b = r_i$ (for every indicator $i$)
- Iterate over indicators $i$, collect contributing button variables $\{x_b \mid A_{i,b}=1\}$, sums them, and add equality to `joltReqs}[i]`.
- Objective min $\sum_b x_b$ (fewest total presses)
  - $\text{totalSum} = \sum_b x_b$ $\to$ `MkMinimize(totalSum)`.

# Rethink Predicates
Current: Predicate<T,R> = Predicate on type T (Fact, Factset), with parameterization bind type R (Factset), returning a boolean.

Dynamic comparisons make the bind(R) required: it this fact.field > [select max value from a set]. Instead of some constant: fact.field > 42. This is only for comparators in practice. For single arg predicates (FactSet??), the arg is retrieved on the fly anyway.

Should comparator become: 
Comparator (e.g. Gt), 

```
new Gt(Rule rowSelect, Rule valSelect, Rule valCmp). 

new Gt("from persons", "select age", const 42)
new Gt("from persons", "select age", "select max age / 2")

apply(Factset arg){
    Factset rows = rowSelect.apply(arg)
    Factset vals = valSelect.apply(rows);
    pred.apply(vals) // first col
    return filtered rows.
}
```

The main issue may be that predicates are (rightfully??) not Rules. They are simple and serve the core FactSet.filter(Predicate<Fact>) well.
FactSets are now useful without the dependency on Rules.

Maybe Rule.Filter, Rule.ReturnIf, ... Should not take a Predicate, but a PredicateRule; a predicate wrapped in a Rule, that is applied to "bind". It returns FS const true and false. For each row. A true Rule.


![Static Badge](https://img.shields.io/badge/JVM-21-orange)


# MaxRegel Core

This repo contains the reference implementation of our rule system, named _MaxRegel_.

A rule engine is a software system that makes decisions based on a set of rules. Instead of writing a lot of complex code, you list the rules, and the engine figures out what to do based on the facts it knows.

A rule engine helps organizations make decisions automatically and consistently. Instead of relying on people to remember and apply many rules by hand, the engine checks information against clear “if this, then that” instructions. This means decisions can be made faster, with fewer mistakes, and in the same way every time. Another benefit is flexibility: rules can be updated or added without changing the whole system, making it easier to adapt when policies, regulations, or business needs change. Overall, a rule engine saves time, reduces errors, and ensures that important decisions follow the right guidelines.

> **_NOTE:_**  The philosophy for this project's design is described in the [MaxRegel book](https://github.com/MinBZK/max-regel-book).

## Installation

- Clone the project

  ```shell script
  git clone git@github.com:zvasva/max-regel-core.git
  cd max-regel-core
  ```

- Install the JDK and maven with [asdf](https://asdf-vm.com/) and its [java plugin](https://github.com/halcyon/asdf-java) and [maven plugin](https://github.com/halcyon/asdf-maven):

  ```shell script
  asdf install java graalvm-community-23.0.1
  asdf global java graalvm-community-23.0.1
  asdf install maven 3.9.9
  asdf global maven 3.9.9
  ```

- Install the maven wrapper

  ```shell script
  mvn wrapper:wrapper
  ```

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```


# Basic ingredients of this system

A short explanation of the most important objects are listed below.

## Term (basic objects)
💡 In the end, when creating rules, we want to think in terms of plain simple objects.
For example those are can be `person`s with a `name` and addresses. Those addresses may in turn be their own simple objects, with a `street`, `city` etc.
Those simple objects are commonly referred to domain objects, model objects, (named) tuples or terms. We use the latter, "Term", to denote a mathematical object from the domain of discourse. [wiki](https://en.wikipedia.org/wiki/Term_(logic))

⚙️ Terms can be implemented as mappings, dicts, tuples, objects. This framework supports them all and build on top of an abstract Term class. It also provides a general `equals` and `hash` function that compares terms in an field-order independent way.


## Facts (Terms with meta data)
💡 When we deal with terms in an inference system, you'll have to take care of certain bookkeeping. What was the source of term? Was it given, or inferred?
We wrap the term in a so-called Fact, to provide this additional metadata, separate from the "core values".

⚙️ A fact is a single concrete class.

## FactSet (A collection of facts)
💡 Multiple facts make up a collection, that can be interrogated.
E.g. is there a fact of type person, that with an age greater than 35?
Or: provide all persons living on the same address.

A factset may contain of several sections, that can be selected by name.
A factset may contain `persons`, `vehicles`, ... Think of them as separate tables.

There are different tools to filter or join factsets, allowing selecting relevant data in order to infer new facts (and therefore factsets).

⚙️A Factset is an abstract class. Different implementations exists, with different backing structures.
A factset can be attached to a database, allowing to support vast sets. Those can be elegantly joined with other database factsets or in-memory and/and ad-hoc factsets, to form a single factset to reason with.

## Rules (Derive new FactSets and trigger side effects)
💡 A rule is a function that takes a factset, and creates new facts. It can also indicate other side effects emerge. Those side effects are not facts themselves, but separate Actions that should be handled outside the core. Such as sending an email or so.

⚙️ A rule is an abstract class.

🤔 TODO: Why is an action not a special Fact again?

## Inference

Given a factset, and a bunch of rules, we can create new facts/insights. If we take the old and new facts together, and run al those rules again, we may in turn end up with even more insigths/facts.
Doing this over and over, until nothing new is added anymore, is the basis of forward chaining inference.

# User services

Added a new maven profile "pitest"
pitest is a tool for analyzing quality of unittests a.k.a mutation testing

It mutates the source code, and runs the tests to see if the change gets caught.

The result is put in target/pit-repots/*.html

```bash
mvn clean install -Ppitest
```
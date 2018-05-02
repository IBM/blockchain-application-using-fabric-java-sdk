
* While running `mvn install` command, you may get error as:

  ![](images/err1.png)

  This error says that there is some problem with `<user home>/.m2` directory. Rename/delete the existing .m2 directory and   rerun the command as:

  ```
  mvn clean install
  ```

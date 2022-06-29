## Contact Manager

It's a toolset for vcard file management, and is developed with javafx framework.
You can use the software freely, if you have experience in javafx development, you can also improve the software together.

### Features

* Fields that support editing
  * FormattedName
  * StructuredName
  * TelephoneNumber
  * Organization (not yet perfect, multi organization should separate by semicolon)
  * Email
  * Url
  * Impp (not yet perfect, only text edits can be used to the original content)
  * Note
  * ProductId (not yet perfect, will be replaced by [mangstadt/ez-vcard](https://github.com/mangstadt/ez-vcard) when saving)
  * Revision (not yet perfect, only date edits can be made to revision, the time can't be specialed)
* Operations that supportted
  * remove whitespaces for FormattedName, StructuredName
  * format telephone numbers
  * remove duplicated contact
* no more unless there is more requirement

### Usage

* Now, you can only use it by compiling it in you os, I will put the executable program on Release Page if I can package this project.
  * ```git clone https://github.com/indiefun/contact-manager.git```
  * open contact-manager/pom.xml in IntelliJ IDEA, and wait for dependences downloaded by maven
  * run MainApplication.main
* click File -> Open to open the .vcf or .vcard files, you can open multi files once or more times, it will append records in current dataset
* edit fields or use toolset provided by this program, like removing duplications, formatting telephones, etc.
* click File -> Save as button to save the results into one vcard file

### Todo

* Package executable program for multi platform (win, mac, linux), but I have no success in it, I think I need help if You can and would.
* Support editing for more fields
* Complete editing perfectly for the fields that not yet perfect
* Making an Icon
* Support keyboard shortcuts for editing

### Screenshot

* Main window
![Main window](https://github.com/indiefun/contact-manager/raw/gh-pages/pics/main-window-01.png)

* Setting window
![Main window](https://github.com/indiefun/contact-manager/raw/gh-pages/pics/setting-window-01.png)


### Documentation



#### Project Structure

```yaml
astminer:
 - src:
 	- main:
 		- antlr:
 			- Java8Lexer.g4
 			- Java8Parser.g4
 			- Python3.g4
 		- generated
 		- java
 		- kotlin: do actual work
 	- test
 - py_example: a classification task
 	- data: dir for input Python files
 	- data_processing:
 		- PathMinerDataset: a tailor-made torch Dataset
 		- PathMinerLoader: a loader for PathMinerDataset
 		- UtilityEntities: PathContext and Path
 	- model:
 		- CodeVectorizer: code2vec model
 		- ProjectClassifier: a classification model
 	- run_example: run the classification example from scratch
 - data: dir for input code files/folders
 - input: dir for input files, needed when generating from exisiting vocabulary
 - output: dir for output files, should contain 4 cvs files
```



#### Mechanism in `/src/main/kotlin/astminer`

The main worker is written in kotlin.

`VocabularyPathStorage` class in `/paths/VocabularyPathStorage` file is the worker of saving generated information by default.

`RegistratedPathStorage` class in `/paths/RegistratedPathStorage` file is used as the worker of saving generated information when generating using existing vocabulary.

Files in `/examples` show the usages of extracting Path-contexts from code of several languages. 



#### Output

+ node_types.csv: (node_id, node_type)
+ paths.csv: (path_id, path_by_node_id)

+ tokens.csv: (token_id, token)

+ path_context.csv: (file_id, path)

  representation of path：``startTokenId pathId endTokenId`

  where `pathId` corresponds to `id` in `paths.csv` , while `startTokenId` and `endTokenId ` corresponds to `id` in `tokens.csv`
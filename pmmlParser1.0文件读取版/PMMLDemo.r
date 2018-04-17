# load library and data
library(randomForest)
data(iris)

# load data and divide(划分) into training set and sampling(训练集和测试集)
# 将数据分为两部分 70%训练集 30%测试集
ind <- sample(2,nrow(iris),replace=TRUE,prob=c(0.7,0.3))
trainData <- iris[ind==1,]
testData <- iris[ind==2,]

# train model
iris_rf <- randomForest(Species~.,data=trainData,ntree=100,proximity=TRUE)
table(predict(iris_rf),trainData$Species)

# visualize the model
print(iris_rf)
attributes(iris_rf)
plot(iris_rf)

# load xml and pmml library
library(XML)
library(pmml)

# convert model to pmml
iris_rf.pmml <- pmml(iris_rf,name="Iris Random Forest",data=iris_rf)

# save to file "iris_rf.pmml" in same workspace
saveXML(iris_rf.pmml,"D://iris_rf.pmml")


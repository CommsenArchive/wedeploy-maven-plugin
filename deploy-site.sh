if mvn clean && mvn -Psite; then
	currentDir=$(pwd)
	cd ../wedeploy-mvan-plugin-site && git add . && git commit -m "Update site" && git push 
	cd $currentDir
fi


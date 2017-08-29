
result = false;

for (i = 0; i <10; i++) {
	println("attempt " + i)
	sleep(1000)
	output = ""
	try {
		output = 'https://maven-plugin-test-javaapitest.wedeploy.io/'.toURL().text;
		println (output)
	} catch (Exception e) {
	}	
	result = output == "It works"
	if (result) {
		break;
	}
}

assert result;

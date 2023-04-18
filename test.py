import os
for dir in os.walk("."):
    cwd = dir[0]
    print(cwd)
    for inputFilename in dir[-1]:
        if(not inputFilename.startswith("input")):
            break
        print(inputFilename)
        with open(cwd+'/'+inputFilename) as f:
            inputStr = f.read()
        if(not inputStr.endswith('\n')):
            with open(cwd+'/'+inputFilename, 'w') as f:
                f.write(inputStr.strip())
                f.write('\n')

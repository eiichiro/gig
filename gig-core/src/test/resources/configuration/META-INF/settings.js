test1 = 'test1'
i = 0
test2 = function() {
	i = i + 1
	return i
}

endpoints = [
	Packages.org.eiichiro.gig.Endpoint5
]

pipeline = new Pipeline()
pipeline.set('Pipeline', new Packages.org.eiichiro.bootleg.Route())

requestTypes = {
	'JSONRequest': Packages.org.eiichiro.bootleg.json.JSONRequest
}

responseTypes = {
	'JSONResponse': Packages.org.eiichiro.bootleg.json.JSONResponse
}

deployment = Emergency

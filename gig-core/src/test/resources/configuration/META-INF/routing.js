routing = new Routing()
routing.ignore('*.js', '*.css', '*.gif', '*.png', '*.jpg', '*.jsp', '*.html')
routing.add('/{user}/hello', Packages.org.eiichiro.gig.Endpoint5, 'hello')
routing.add(Verb.GET, '/{user}/goodbye', Packages.org.eiichiro.gig.Endpoint5, 'goodbye')

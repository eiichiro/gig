package org.eiichiro.gig.appengine;

import static org.junit.Assert.*;

import org.eiichiro.gig.Gig;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.backends.BackendService;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.capabilities.CapabilitiesService;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.labs.modules.ModulesService;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.prospectivesearch.ProspectiveSearchService;
import com.google.appengine.api.quota.QuotaService;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@SuppressWarnings("deprecation")
public class AppEngineServiceComponentTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper();
	
	@Before
	public void setUp() throws Exception {
		helper.setUp();
		Gig.bootstrap();
	}

	@After
	public void tearDown() throws Exception {
		Gig.shutdown();
		helper.tearDown();
	}

	@Inject private AppIdentityService appIdentityService;
	
	@Inject private AsyncDatastoreService asyncDatastoreService;
	
	@Inject private AsyncMemcacheService asyncMemcacheService;
	
	@Inject private BackendService backendService;
	
	@Inject private BlobstoreService blobstoreService;
	
	@Inject private CapabilitiesService capabilitiesService;
	
	@Inject private ChannelService channelService;
	
//	@Inject private ConversionService conversionService;
	
	@Inject private DatastoreService datastoreService;
	
	@Inject private FileService fileService;
	
	@Inject private ImagesService imagesService;
	
	@Inject private SearchService searchService;
	
	@Inject private MailService mailService;
	
	@Inject private MemcacheService memcacheService;
	
	@Inject private OAuthService oAuthService;
	
	@Inject private ProspectiveSearchService prospectiveSearchService;
	
	@Inject private Queue queue;
	
	@Inject private QuotaService quotaService;
	
	@Inject private URLFetchService urlFetchService;
	
	@Inject private UserService userService;
	
	@Inject private XMPPService xmppService;
	
	@Inject private ModulesService modulesService;
	
	@Test
	public void testInstance() {
		Jaguar.assemble(this);
		assertNotNull(appIdentityService);
		assertNotNull(asyncDatastoreService);
		assertNotNull(asyncMemcacheService);
		assertNotNull(backendService);
		assertNotNull(blobstoreService);
		assertNotNull(capabilitiesService);
		assertNotNull(channelService);
//		assertNotNull(conversionService);
		assertNotNull(datastoreService);
		assertNotNull(fileService);
		assertNotNull(imagesService);
		assertNotNull(searchService);
		assertNotNull(mailService);
		assertNotNull(memcacheService);
		assertNotNull(oAuthService);
		assertNotNull(prospectiveSearchService);
		assertNotNull(queue);
		assertNotNull(quotaService);
		assertNotNull(urlFetchService);
		assertNotNull(userService);
		assertNotNull(xmppService);
		assertNotNull(modulesService);
	}

}

package org.kyojo.plugin.schemaOrgLab

import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.schemaorg.SimpleJsonBuilder

class LocalBusinessLab {

	String jsonLd

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		// quoted from https://developers.google.com/search/docs/data-types/local-business
		jsonLd = '''
{
	"@context": "http://schema.org",
	"@type": "Restaurant",
	"image": [
		"https://example.com/photos/1x1/photo.jpg",
		"https://example.com/photos/4x3/photo.jpg",
		"https://example.com/photos/16x9/photo.jpg"
	],
	"@id": "http://davessteakhouse.example.com",
	"name": "Dave's Steak House",
	"address": {
		"@type": "PostalAddress",
		"streetAddress": "148 W 51st St",
		"addressLocality": "New York",
		"addressRegion": "NY",
		"postalCode": "10019",
		"addressCountry": "US"
	},
	"geo": {
		"@type": "GeoCoordinates",
		"latitude": 40.761293,
		"longitude": -73.982294
	},
	"url": "http://www.example.com/restaurant-locations/manhattan",
	"telephone": "+12122459600",
	"openingHoursSpecification": [
		{
			"@type": "OpeningHoursSpecification",
			"dayOfWeek": [
				"Monday",
				"Tuesday"
			],
			"opens": "11:30",
			"closes": "22:00"
		},
		{
			"@type": "OpeningHoursSpecification",
			"dayOfWeek": [
				"Wednesday",
				"Thursday",
				"Friday"
			],
			"opens": "11:30",
			"closes": "23:00"
		},
		{
			"@type": "OpeningHoursSpecification",
			"dayOfWeek": "Saturday",
			"opens": "16:00",
			"closes": "23:00"
		},
		{
			"@type": "OpeningHoursSpecification",
			"dayOfWeek": "Sunday",
			"opens": "16:00",
			"closes": "22:00"
		}
	],
	"menu": "http://www.example.com/menu",
	"acceptsReservations": "True"
}
'''

		return null
	}

}

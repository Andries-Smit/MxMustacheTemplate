// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package mxmustachetemplate.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheResolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Populate a Mustache template string with data from a JSON string. Partial templates can be used, these are named and added in the PartialTemplateStrings list.
 */
public class FillTemplateJSON extends CustomJavaAction<java.lang.String>
{
	private java.lang.String JSONString;
	private java.lang.String TemplateString;
	private java.util.List<IMendixObject> __PartialTemplateStrings;
	private java.util.List<mxmustachetemplate.proxies.Template> PartialTemplateStrings;

	public FillTemplateJSON(IContext context, java.lang.String JSONString, java.lang.String TemplateString, java.util.List<IMendixObject> PartialTemplateStrings)
	{
		super(context);
		this.JSONString = JSONString;
		this.TemplateString = TemplateString;
		this.__PartialTemplateStrings = PartialTemplateStrings;
	}

	@java.lang.Override
	public java.lang.String executeAction() throws Exception
	{
		this.PartialTemplateStrings = new java.util.ArrayList<mxmustachetemplate.proxies.Template>();
		if (__PartialTemplateStrings != null)
			for (IMendixObject __PartialTemplateStringsElement : __PartialTemplateStrings)
				this.PartialTemplateStrings.add(mxmustachetemplate.proxies.Template.initialize(getContext(), __PartialTemplateStringsElement));

		// BEGIN USER CODE
		if (this.TemplateString == null) {
			throw new IllegalArgumentException("TemplateString can not be empty");
		}
		if (this.JSONString == null) {
			throw new IllegalArgumentException("JSONString can not be empty");
		}

		// create our MustacheFactory using a custom resolver we've written to resolve Mendix partial templates
		MxResolver mxResolver = new MxResolver();
		MustacheFactory mf = new DefaultMustacheFactory(mxResolver);

		// load in the partials to our MxResolver
		for (mxmustachetemplate.proxies.Template template : this.PartialTemplateStrings) {
			mxResolver.addPartial(template.getName(), template.getTemplate());
		}

		// add our main template. We give this a random name (using a UUID) so it shouldn't clash with any partials.
		StringReader templateStringReader = new StringReader(this.TemplateString);
		Mustache mustache = mf.compile(templateStringReader, UUID.randomUUID().toString());	

		// convert the JSON string to a Hashmap
		Map<String, Object> gsonMap = new HashMap<String, Object>();
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		gsonMap = (Map<String, Object>) gson.fromJson(this.JSONString, gsonMap.getClass());

		// pass the hashmap instead of a class object
		StringWriter output = new StringWriter();
		mustache.execute(output, gsonMap).flush();

		// return the string
		return output.toString();
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "FillTemplateJSON";
	}

	// BEGIN EXTRA CODE
	
	// We need to create our own resolver for so we can return partials for rendering.
	// To do this we are just using a HashMap to store and retrive the data. 
	public class MxResolver implements MustacheResolver {
		protected HashMap<String, StringReader> partials = new HashMap<String, StringReader>();

		public void addPartial(String name, String template) {
			if (template == null) {
				throw new IllegalArgumentException("template attribute can not be empty");
			}
			if (name == null || name.trim().isEmpty()) {
				throw new IllegalArgumentException("name attribute can not be empty");
			}

			StringReader tsreader = new StringReader(template);
			partials.put(name, tsreader);
		}

		@Override
		public Reader getReader(String arg0) {
			return partials.get(arg0);
		}
	}
	// END EXTRA CODE
}

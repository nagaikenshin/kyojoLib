package org.kyojo.core;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.gson.JsonParseException;
import org.kyojo.gson.JsonSyntaxException;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;
import org.kyojo.schemaorg.SimpleJsonBuilder;

public class ReferenceEntry<T> {

	private static final Log logger = LogFactory.getLog(ReferenceEntry.class);

	private String[] vals;
	private String[] valsUtf8;
	private List<FileItem> fis;
	private boolean utf8Flg;
	private Class<T> cls;
	private List<T> list;
	private String mnn;
	private String suf; // ToDo: Entryのsufは意味がない？
	private boolean isList = true;
	private boolean isFile;

	@SuppressWarnings("unchecked")
	public ReferenceEntry(String val) {
		this.vals = new String[] { val };
		this.cls = (Class<T>)String.class;
		isList = false;
	}

	public ReferenceEntry(String[] vals, boolean utf8Flg, Class<T> cls) {
		this.vals = vals;
		this.utf8Flg = utf8Flg;
		this.cls = cls;
	}

	public ReferenceEntry(List<FileItem> fis, boolean utf8Flg, Class<T> cls) {
		this.fis = fis;
		this.utf8Flg = utf8Flg;
		this.cls = cls;
		isFile = true;
	}

	public ReferenceEntry(String mnn, String suf, Class<T> cls) {
		this.mnn = mnn;
		this.suf = suf;
		this.cls = cls;
		if(mnn == null || mnn.length() < 1 || !mnn.startsWith("[")) {
			isList = false;
		}
	}

	public ReferenceEntry(T obj, Class<T> cls) {
		this(obj, null, cls);
	}

	public ReferenceEntry(List<T> list, Class<T> cls) {
		this(list, null, cls);
	}

	public ReferenceEntry(T obj, String suf, Class<T> cls) {
		List<T> list = new ArrayList<>();
		list.add(obj);
		this.list = list;
		this.suf = suf;
		this.cls = cls;
		isList = false;
	}

	public ReferenceEntry(List<T> list, String suf, Class<T> cls) {
		this.list = list;
		this.suf = suf;
		this.cls = cls;
	}

//	@SuppressWarnings("unchecked")
//	private Class<T> getListFieldClass() {
//		try {
//			// Field fld = getClass().getDeclaredField("list");
//			// ParameterizedType gType = (ParameterizedType)fld.getGenericType();
//			// Type[] aTypes = gType.getActualTypeArguments();
//			// Class<T> cls = (Class<T>)aTypes[0];
//
//			return cls;
//		} catch(NoSuchFieldException nsfe) {
//		} catch(SecurityException se) {
//		}
//
//		return null;
//	}

	private void encVals() {
		if(valsUtf8 != null) {
			return;
		} else if(vals != null) {
			if(utf8Flg) {
				valsUtf8 = new String[vals.length];
				try {
					for(int idx = 0; idx < vals.length; idx++) {
						valsUtf8[idx] = new String(vals[idx].getBytes("ISO-8859-1"), "UTF-8");
					}
				} catch(UnsupportedEncodingException usee) {}
			} else {
				valsUtf8 = vals;
			}
		} else if(fis != null) {
			valsUtf8 = new String[fis.size()];
			if(utf8Flg) {
				try {
					for(int idx = 0; idx < fis.size(); idx++) {
						valsUtf8[idx] = new String(fis.get(idx).get(), "UTF-8");
					}
				} catch(UnsupportedEncodingException usee) {}
			} else {
				try {
					for(int idx = 0; idx < fis.size(); idx++) {
						valsUtf8[idx] = fis.get(idx).getString("UTF-8");
					}
				} catch(UnsupportedEncodingException usee) {}
			}
		}
	}

	public T getObj() {
		if(list != null && list.size() > 0) {
			return list.get(0);
		}

		getList();
		if(list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<T> getList() {
		if(list != null) {
			return list;
		}

		if((vals != null || fis != null) && cls != null) {
			convValsToList();
		} else if(mnn != null && cls != null) {
			convMnnToList();
		} else {
			list = null;
		}

		return list;
	}

	public String getVal() {
		if((vals != null && vals.length > 0) || (fis != null && fis.size() > 0)) {
			encVals();
			return valsUtf8[0];
		}

		if(list != null && list.size() > 0) {
			convListToVals();
		}

		if(vals != null && vals.length > 0) {
			encVals();
			return valsUtf8[0];
		} else {
			return null;
		}
	}

	public byte[] getRaw() {
		if(vals != null && vals.length > 0) {
			return vals[0].getBytes();
		} else if(fis != null && fis.size() > 0) {
			return fis.get(0).get();
		}

		if(list != null && list.size() > 0) {
			convListToVals();
		}

		if(vals != null && vals.length > 0) {
			return vals[0].getBytes();
		} else {
			return null;
		}
	}

	public List<byte[]> getRawList() {
		List<byte[]> raws = new ArrayList<>();
		if(vals != null && vals.length > 0) {
			for(String val : vals) {
				byte[] raw = val.getBytes();
				raws.add(raw);
			}
			return raws;
		} else if(fis != null && fis.size() > 0) {
			for(FileItem fi : fis) {
				byte[] raw = fi.get();
				raws.add(raw);
			}
			return raws;
		}

		if(list != null && list.size() > 0) {
			convListToVals();
		}

		if(vals != null && vals.length > 0) {
			for(String val : vals) {
				byte[] raw = val.getBytes();
				raws.add(raw);
			}
			return raws;
		} else {
			return null;
		}
	}

	public String getMnn() {
		return getMnn(isList);
	}

	public String getMnn(boolean isList) {
		if(mnn != null) {
			return mnn;
		}

		if(list == null && (vals != null || fis != null)) {
			convValsToList();
		}
		if(list != null) {
			convListToMnn(isList);
		}

		return mnn;
	}

	public String getSuf() {
		if(suf != null) {
			return suf;
		}

		// getMnn();
		// if(suf == null) {
			getObj();
		// }

		return suf;
	}

	public Class<T> getCls() {
//		if(cls == null) {
//			cls = getListFieldClass();
//		}

		return cls;
	}

	public boolean getIsList() {
		return isList;
	}

	public boolean getIsFile() {
		return isFile;
	}

	public boolean getUtf8Flg() {
		return utf8Flg;
	}

	public FileItem getFileItem() {
		if(fis != null) return fis.get(0);
		return null;
	}

	public List<FileItem> getFileItemList() {
		return fis;
	}

	private void convValsToList() {
		list = new ArrayList<>();
		encVals();
		for(String val : valsUtf8) {
			T obj = ReferenceConverter.convVal2Obj(val, cls);
			list.add(obj);
		}
		suf = ReferenceConverter.convCls2Suf(cls);
	}

	private void convListToVals() {
		vals = new String[list.size()];
		for(int idx = 0; idx < list.size(); idx++) {
			T obj = list.get(idx);
			String val = ReferenceConverter.convObj2Val(obj, cls);
			vals[idx] = val;
		}
		suf = ReferenceConverter.convCls2Suf(getCls());
	}

	private void convMnnToList() {
		if(mnn.length() == 0) {
			list = null;
		} else if(mnn.startsWith("[")) {
			try {
				Type listType = TypeToken.getParameterized(ArrayList.class, cls).getType();
				list = My.deminion(mnn, listType);
			} catch(JsonSyntaxException jse) {
				logger.warn("JsonSyntaxException: " + jse.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
				logger.warn("class: " + cls.getName());
				list = null;
			} catch(JsonParseException jpe) {
				logger.warn("JsonParseException: " + jpe.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
				logger.warn("class: " + cls.getName());
				list = null;
			} catch(Exception ex) {
				logger.warn("Exception: " + ex.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
				logger.warn("class: " + cls.getName());
				list = null;
			}
		} else {
			try {
				T obj = My.deminion(mnn, cls);
				list = new ArrayList<>();
				list.add(obj);
			} catch(JsonSyntaxException jse) {
				logger.warn("JsonSyntaxException: " + jse.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
				logger.warn("class: " + cls.getName());
				list = null;
			} catch(JsonParseException jpe) {
				logger.warn("JsonParseException: " + jpe.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
				logger.warn("class: " + cls.getName());
				list = null;
			} catch(Exception ex) {
				logger.warn("Exception: " + ex.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
				logger.warn("class: " + cls.getName());
				list = null;
			}
		}
		suf = Constants.MINION_SUFFIX;
	}

	private void convListToMnn(boolean isList) {
		if(isList) {
			mnn = SimpleJsonBuilder.toJson(list, List.class);
		} else if(list.size() == 0) {
			mnn = "{}";
		} else {
			mnn = SimpleJsonBuilder.toJson(list.get(0), cls);
		}
	}

}

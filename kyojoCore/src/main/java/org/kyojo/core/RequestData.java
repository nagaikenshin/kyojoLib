package org.kyojo.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.gson.JsonParseException;
import org.kyojo.gson.JsonSyntaxException;
import org.kyojo.minion.My;
import org.kyojo.schemaorg.NativeValueDataType;

public class RequestData implements Map<String, Object> {

	private static final Log logger = LogFactory.getLog(RequestData.class);

	private HttpServletRequest request;

	private Map<String, Object> map;

	private GlobalData gbd;

	private HashMap<String, ReferenceStructure> bltMap;

	private Map<String, String[]> bfDlmMap;

	private boolean flgAjax = false;

	public boolean isAjax() {
		return flgAjax;
	}

	private boolean isLogAllParams = false;

	private boolean submitNeedEncode = false;

	private boolean ajaxNeedEncode = false;

	private List<FileItem> fiList = null;

	public RequestData(HttpServletRequest request, GlobalData gbd) {
		this.gbd = gbd;
		Object val = gbd.get("IS_LOG_ALL_PARAMS");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			isLogAllParams = true;
		}
		val = gbd.get("SUBMIT_NEED_ENCODE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			submitNeedEncode = true;
		}
		val = gbd.get("AJAX_NEED_ENCODE");
		if(val != null && !val.toString().equals("0") && !val.toString().equalsIgnoreCase("false")) {
			ajaxNeedEncode = true;
		}

		this.request = request;
		String ext = request.getParameter(Constants.EXT_KEY);
		if(ext != null && ext.equals(Constants.AJAX_EXT)) {
			flgAjax = true;
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		condBuildMap();
		String lwrKey = key.toString().toLowerCase();
		return map.containsKey(lwrKey);
	}

	private void condBuildBfDlmMap() {
		if(bfDlmMap != null) return;

		bfDlmMap = new HashMap<>();
		for(String key : request.getParameterMap().keySet()) {
			if(key.startsWith(Constants.PLG_SUBMIT_MTD_PREFIX)) {
				int dlmPos = key.indexOf('/');
				if(dlmPos < 1) {
					bfDlmMap.put(key, new String[] { key });
				} else {
					bfDlmMap.put(key.substring(0, dlmPos), new String[] { key, key.substring(dlmPos, key.length()) });
				}
			}
		}

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {
			try {
				if(fiList == null) {
					DiskFileItemFactory factory = new DiskFileItemFactory();
					ServletContext servletContext = request.getServletContext();
					File repository = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
					factory.setRepository(repository);
					ServletFileUpload upload = new ServletFileUpload(factory);
					fiList = upload.parseRequest(request);
				}
				for(FileItem fi : fiList) {
					String key = fi.getFieldName();
					if(key.startsWith(Constants.PLG_SUBMIT_MTD_PREFIX)) {
						int dlmPos = key.indexOf('/');
						if(dlmPos < 1) {
							bfDlmMap.put(key, new String[] { key });
						} else {
							bfDlmMap.put(key.substring(0, dlmPos), new String[] { key, key.substring(dlmPos, key.length()) });
						}
					}
				}
			} catch(FileUploadException fue) {
				logger.error(fue.getMessage(), fue);
			}
		}
	}

	public boolean containsKeyBfDlm(String key) {
		condBuildBfDlmMap();
		return bfDlmMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object vals) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		condBuildMap();
		return map.entrySet();
	}

	@Override
	public Object get(Object key) {
		condBuildMap();
		String lwrKey = key.toString().toLowerCase();
		return map.get(lwrKey);
	}

	public String getValueBfDlm(String key) {
		condBuildBfDlmMap();
		return request.getParameterMap().get(bfDlmMap.get(key)[0])[0];
	}

	public String getOrigKeyBfDlm(String key) {
		condBuildBfDlmMap();
		return bfDlmMap.get(key)[0];
	}

	public String getKeyAfDlm(String key) {
		condBuildBfDlmMap();
		String[] tmpElms = bfDlmMap.get(key);
		if(tmpElms == null || tmpElms.length < 2) {
			return null;
		} else {
			return tmpElms[1];
		}
	}

	@Override
	public boolean isEmpty() {
		condBuildMap();
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		condBuildMap();
		return map.keySet();
	}

	@Override
	public ReferenceStructure put(String key, Object vals) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ReferenceStructure remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		condBuildMap();
		return map.size();
	}

	@Override
	public Collection<Object> values() {
		condBuildMap();
		return map.values();
	}

	@SuppressWarnings("unchecked")
	public void condBuildMap() {
		if(map != null) return;
		map = new HashMap<>();
		bltMap = new HashMap<>();
		boolean utf8Flg = flgAjax ? ajaxNeedEncode : submitNeedEncode;

		Map<String, Object> tmap = new HashMap<>();
		if(isLogAllParams) {
			List<String> keys = new ArrayList<>(request.getParameterMap().keySet());
			Collections.sort(keys);
			for(String key : keys) {
				String[] ts = request.getParameterMap().get(key);
				int idx = 0;
				for(String t : ts) {
					int len = t.getBytes().length;
					if(len < 10000) {
						logger.info("rqd-raw: " + key + "[" + idx + "]=" + t);
					} else {
						logger.info("rqd-raw: " + key + "[" + idx + "]=(" + len + "bytes)");
					}
					idx++;
				}
			}
		}

		// リクエストパラメーターをツリー状に解釈
		for(Map.Entry<String, String[]> ent : request.getParameterMap().entrySet()) {
			if(ent.getKey().startsWith("_")) {
				// _で始まっているものは無視する
				continue;
			}

			String[] kes = ent.getKey().replaceAll("\\]", "").split("[\\.\\[]");
			Map<String, Object> tmapPtr = tmap;
			int ki = 0;
			String ke = "";
			for(; ki < kes.length - 1; ki++) {
				ke = kes[ki];
				if(!tmapPtr.containsKey(ke)) {
					tmapPtr.put(ke, new HashMap<String, Object>());
				}
				tmapPtr = (Map<String, Object>)tmapPtr.get(ke);
			}
			ke = kes[ki];
			tmapPtr.put(ke, ent.getValue());
		}

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {
			try {
				if(fiList == null) {
					DiskFileItemFactory factory = new DiskFileItemFactory();
					ServletContext servletContext = request.getServletContext();
					File repository = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
					factory.setRepository(repository);
					ServletFileUpload upload = new ServletFileUpload(factory);
					fiList = upload.parseRequest(request);
				}
				Map<String, List<FileItem>> fiMap = new HashMap<>();
				for(FileItem fi : fiList) {
					String key = fi.getFieldName();
					List<FileItem> tmpList;
					if(fiMap.containsKey(key)) {
						tmpList = fiMap.get(key);
					} else {
						tmpList = new ArrayList<>();
						fiMap.put(key, tmpList);
					}
					tmpList.add(fi);
				}

				if(isLogAllParams) {
					List<String> keyList = new ArrayList<>(fiMap.keySet());
					Collections.sort(keyList);
					for(String key : keyList) {
						List<FileItem> tmpList = fiMap.get(key);
						int idx = 0;
						for(FileItem fi : tmpList) {
							if(fi.getSize() < 1024) {
								logger.info("rqd-raw: " + key + "[" + idx + "]=" + fi.getString());
							} else {
								logger.info("rqd-raw: " + key + "[" + idx + "]=(" + fi.getSize() + "bytes)");
							}
							idx++;
						}
					}
				}

				// リクエストパラメーターをツリー状に解釈
				for(Map.Entry<String, List<FileItem>> ent : fiMap.entrySet()) {
					if(ent.getKey().startsWith("_")) {
						// _で始まっているものは無視する
						continue;
					}

					String[] kes = ent.getKey().replaceAll("\\]", "").split("[\\.\\[]");
					Map<String, Object> tmapPtr = tmap;
					int ki = 0;
					String ke = "";
					for(; ki < kes.length - 1; ki++) {
						ke = kes[ki];
						if(!tmapPtr.containsKey(ke)) {
							tmapPtr.put(ke, new HashMap<String, Object>());
						}
						tmapPtr = (Map<String, Object>)tmapPtr.get(ke);
					}
					ke = kes[ki];
					tmapPtr.put(ke, ent.getValue());
				}
			} catch(FileUploadException fue) {
				logger.error(fue.getMessage(), fue);
			}
		}

		map = (Map<String, Object>)condBuildMapSub(tmap, "", utf8Flg);

//		for(Map.Entry<String, ReferenceStructure> ent : bltMap.entrySet()) {
//			String[] kes = ent.getKey().split("\\.");
//			Map<String, Object> tmapPtr = map;
//			int ki = 0;
//			String ke = "";
//			for(; ki < kes.length - 1; ki++) {
//				ke = kes[ki];
//				if(!tmapPtr.containsKey(ke)) {
//					tmapPtr.put(ke, new HashMap<String, Object>());
//				}
//				tmapPtr = (Map<String, Object>)tmapPtr.get(ke);
//			}
//			ke = kes[ki];
//			tmapPtr.put(ke, ent.getValue());
//		}

		if(isLogAllParams) {
			List<String> keys = new ArrayList<>(bltMap.keySet());
			Collections.sort(keys);
			for(String key : keys) {
				ReferenceStructure ref = (ReferenceStructure)bltMap.get(key);
				if(ref.getIsFile()) {
					List<FileItem> fis = ref.getFileItemList();
					for(int idx = 0; idx < fis.size(); idx++) {
						FileItem fi = fis.get(idx);
						if(fi.getSize() < 1024) {
							if(ref.getUtf8Flg()) {
								try {
									logger.info("rqd-blt: " + key + "[" + idx + "]=" + new String(fi.get(), "UTF-8"));
								} catch(UnsupportedEncodingException usee) {}
							} else {
								try {
									logger.info("rqd-blt: " + key + "[" + idx + "]=" + fi.getString("UTF-8"));
								} catch(UnsupportedEncodingException usee) {}
							}
						} else {
							logger.info("rqd-blt: " + key + "[" + idx + "]=(" + fi.getSize() + "bytes)");
						}
					}
				} else {
					logger.info("rqd-blt: " + key + "=" + ref.getMnn());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Object condBuildMapSub(Object val, String ns, boolean utf8Flg) {
		if(val == null) {
			return null;
		} else if(val.getClass().isArray()) {
			// リクエストパラメータのキーを分解した末端
			return new ReferenceStructure((String[])val, utf8Flg);
		} else if(val instanceof List) {
			// リクエストパラメータのキーを分解した末端
			return new ReferenceStructure((List<FileItem>)val, utf8Flg);
		}
		Map<String, Object> smap = (Map<String, Object>)val;

		// 無視するキー
		if(smap.size() == 1 && smap.containsKey(Constants.DEMION_SUFFIX)) {
			return null;
		}
		smap.remove(Constants.DEMION_SUFFIX);

		// リクエストパラメータのキーを分解した途中
		Map<Integer, Object> imap = new HashMap<>();
		int maxNo = 0;
		for(Map.Entry<String, Object> ent : smap.entrySet()) {
			if(ent.getKey().matches("^\\d+$")) {
				int no = Integer.parseInt(ent.getKey());
				if(no == 0) {
					maxNo = -1;
					break;
				} else {
					imap.put(no - 1, ent.getValue());
					if(maxNo < no) {
						maxNo = no;
					}
				}
			} else {
				maxNo = -1;
				break;
			}
		}

		if(maxNo > 0) {
			// キーが数字のみならList型
			List<Object> list = new ArrayList<>();
			for(Map.Entry<Integer, Object> ent : imap.entrySet()) {
				Object obj = condBuildMapSub(ent.getValue(), ns + (ns.length() == 0 ? "" : ".") + ent.getKey(), utf8Flg);
				if(obj != null) {
					list.add(obj); // nullは詰める。入力欄があればnullにはならない（nullはdemionの場合のみ？）
				}
			}
			// map.put(ns.toLowerCase() + "." + Constants.MINION_SUFFIX.toLowerCase(),
			//		ReferenceConverter.genStructureAssortedType(list, Constants.MINION_SUFFIX, Object.class));
			return list;
		} else {
			if(smap.containsKey(Constants.MINION_SUFFIX)) {
				// minionが送られてきたら優先
				Object tmpObj = smap.get(Constants.MINION_SUFFIX);
				if(tmpObj.getClass().isArray()) {
					String[] vals = (String[])tmpObj;
					if(utf8Flg) {
						try {
							String mnn = new String(vals[0].getBytes("ISO-8859-1"), "UTF-8");
							if(smap.size() == 1) {
								return new ReferenceStructure(mnn, Constants.MINION_SUFFIX);
							} else {
								try {
									Map<String, Object> tmpMap = My.deminion(mnn, Map.class);
									TemplateEngine.buildMagicFloorRecursive(gbd, null, tmpMap, null, null, null, null,
											ns + ".", bltMap, null, 0, 3, 0);
								} catch(JsonSyntaxException jse) {
									logger.warn("JsonSyntaxException: " + jse.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								} catch(JsonParseException jpe) {
									logger.warn("JsonParseException: " + jpe.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								} catch(Exception ex) {
									logger.warn("Exception: " + ex.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								}
							}
						} catch(UnsupportedEncodingException usee) {}
					} else {
						if(smap.size() == 1) {
							return new ReferenceStructure(vals[0], Constants.MINION_SUFFIX);
						} else {
							try {
								Map<String, Object> tmpMap = My.deminion(vals[0], Map.class);
								TemplateEngine.buildMagicFloorRecursive(gbd, null, tmpMap, null, null, null, null,
										ns + ".", bltMap, null, 0, 3, 0);
							} catch(JsonSyntaxException jse) {
								logger.warn("JsonSyntaxException: " + jse.getMessage());
								logger.warn("json: " + StringUtils.abbreviate(vals[0], 1024));
								logger.warn("class: " + Map.class.getName());
							} catch(JsonParseException jpe) {
								logger.warn("JsonParseException: " + jpe.getMessage());
								logger.warn("json: " + StringUtils.abbreviate(vals[0], 1024));
								logger.warn("class: " + Map.class.getName());
							} catch(Exception ex) {
								logger.warn("Exception: " + ex.getMessage());
								logger.warn("json: " + StringUtils.abbreviate(vals[0], 1024));
								logger.warn("class: " + Map.class.getName());
							}
						}
					}
				} else {
					List<FileItem> fis = (List<FileItem>)tmpObj;
					if(utf8Flg) {
						try {
							String mnn = new String(fis.get(0).get(), "UTF-8");
							if(smap.size() == 1) {
								return new ReferenceStructure(mnn, Constants.MINION_SUFFIX);
							} else {
								try {
									Map<String, Object> tmpMap = My.deminion(mnn, Map.class);
									TemplateEngine.buildMagicFloorRecursive(gbd, null, tmpMap, null, null, null, null,
											ns + ".", bltMap, null, 0, 3, 0);
								} catch(JsonSyntaxException jse) {
									logger.warn("JsonSyntaxException: " + jse.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								} catch(JsonParseException jpe) {
									logger.warn("JsonParseException: " + jpe.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								} catch(Exception ex) {
									logger.warn("Exception: " + ex.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								}
							}
						} catch(UnsupportedEncodingException usee) {}
					} else {
						// ToDo: AjaxでFileItemの場合だから不要な気がする
						try {
							String mnn = fis.get(0).getString("UTF-8");
							if(smap.size() == 1) {
								return new ReferenceStructure(mnn, Constants.MINION_SUFFIX);
							} else {
								try {
									Map<String, Object> tmpMap = My.deminion(mnn, Map.class);
									TemplateEngine.buildMagicFloorRecursive(gbd, null, tmpMap, null, null, null, null,
											ns + ".", bltMap, null, 0, 3, 0);
								} catch(JsonSyntaxException jse) {
									logger.warn("JsonSyntaxException: " + jse.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								} catch(JsonParseException jpe) {
									logger.warn("JsonParseException: " + jpe.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								} catch(Exception ex) {
									logger.warn("Exception: " + ex.getMessage());
									logger.warn("json: " + StringUtils.abbreviate(mnn, 1024));
									logger.warn("class: " + Map.class.getName());
								}
							}
						} catch(UnsupportedEncodingException usee) {}
					}
				}
			}

			// Object型
			Map<String, Object> omap = new HashMap<>();
			for(Map.Entry<String, Object> ent : smap.entrySet()) {
				if(ent.getKey().equals(Constants.MINION_SUFFIX)) {
					continue;
				}
				String key = ns + (ns.length() == 0 ? "" : ".") + ent.getKey();
				Object obj = condBuildMapSub(ent.getValue(), key, utf8Flg);
				if(obj == null) {
					continue; // nullは詰める。入力欄があればnullにはならない（nullはdemionの場合のみ？）
				} else if(obj instanceof ReferenceStructure) {
					// 末端の要素
					// map.put(key.toLowerCase(), (ReferenceStructure)obj);
					// if(isLogAllParams) {
						bltMap.put(key.toLowerCase(), (ReferenceStructure)obj);
					// }
				} else {
					// omap.put(ent.getKey(), obj);
					// 途中の要素
					// if(obj.getClass().isArray()) {
					//	map.put(key.toLowerCase(), new ReferenceStructure((String[])obj));
					// }
					// map.put(key.toLowerCase() + "." + Constants.MINION_SUFFIX.toLowerCase(),
					//	ReferenceConverter.genStructureAssortedType(obj, Constants.MINION_SUFFIX, obj.getClass()));
				}
				omap.put(ent.getKey().toLowerCase(), obj);
			}

			for(NativeValueDataType nvdt : NativeValueDataType.values()) {
				if(smap.containsKey(nvdt.getSuffix())) {
					Object tmpObj = smap.get(nvdt.getSuffix());
					try {
						if(tmpObj.getClass().isArray()) {
							String[] vals = (String[])smap.get(nvdt.getSuffix());
							ReferenceStructure ref = new ReferenceStructure(vals, utf8Flg,
									nvdt.getSuffix(), nvdt.getNativeValueClass());
							// omap.put(nls.getKey(), vals);
							omap.put(nvdt.getSuffix().toLowerCase() + "list", ref);
						} else if(tmpObj instanceof List) {
							List<FileItem> fis = (List<FileItem>)tmpObj;
							ReferenceStructure ref = new ReferenceStructure(fis, utf8Flg,
									nvdt.getSuffix(), nvdt.getNativeValueClass());
							omap.put(nvdt.getSuffix().toLowerCase() + "list", ref);
						} else {
							// Container.Textはここに来る
						}
					} catch(ClassCastException cce) {
						logger.warn("class: " + tmpObj.getClass().getName());
						logger.warn("value: " + tmpObj.toString());
						logger.warn(cce.getMessage(), cce);
					}
				}
			}

			if(ns.length() > 0) {
				if(omap.size() == 0) {
					// map.put(ns.toLowerCase(), null);
				} else {
					for(Map.Entry<String, Object> ent : omap.entrySet()) {
						// map.put((ns + "." + ent.getKey()).toLowerCase(),
						//		ReferenceConverter.genStructureAssortedType(ent.getValue(), Constants.MINION_SUFFIX, ent.getValue().getClass()));
						if(ent.getValue() instanceof ReferenceStructure) {
							// map.put((ns + "." + ent.getKey()).toLowerCase(), (ReferenceStructure)ent.getValue());
							// if(isLogAllParams) {
								bltMap.put((ns + "." + ent.getKey()).toLowerCase(), (ReferenceStructure)ent.getValue());
							// }
						}
					}
				}
			}
			return omap;
		}
	}

}

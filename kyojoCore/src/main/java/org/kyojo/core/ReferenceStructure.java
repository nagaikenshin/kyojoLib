package org.kyojo.core;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.fileupload.FileItem;

public class ReferenceStructure {

	private String[] vals;
	private List<FileItem> fis;
	private boolean utf8Flg;
	private String mnn;
	private String suf;
	private Map<Class<?>, ReferenceEntry<?>> map = new HashMap<>();
	private boolean isFile;
	private Class<? extends Converter> cnvCls = null;

	public ReferenceStructure(String val) {
		this.vals = new String[] { val };
	}

	public ReferenceStructure(String[] vals, boolean utf8Flg) {
		this.vals = vals;
		this.utf8Flg = utf8Flg;
	}

	public <T> ReferenceStructure(String[] vals, boolean utf8Flg, String suf, Class<T> cls) {
		ReferenceEntry<T> ent = new ReferenceEntry<T>(vals, utf8Flg, cls);
		map.put(cls, ent);
		this.utf8Flg = utf8Flg;
		this.suf = suf;
	}

	public ReferenceStructure(List<FileItem> fis, boolean utf8Flg) {
		this.fis = fis;
		this.utf8Flg = utf8Flg;
		isFile = true;
	}

	public <T> ReferenceStructure(List<FileItem> fis, boolean utf8Flg, String suf, Class<T> cls) {
		ReferenceEntry<T> ent = new ReferenceEntry<T>(fis, utf8Flg, cls);
		map.put(cls, ent);
		this.utf8Flg = utf8Flg;
		this.suf = suf;
		isFile = true;
	}

	public ReferenceStructure(String mnn, String suf) {
		this.mnn = mnn;
		this.suf = suf;
	}

	public <T> ReferenceStructure(T obj, String suf, Class<T> cls) {
		ReferenceEntry<?> ent = ReferenceConverter.genEntryAssortedType(obj, cls);
		map.put(ent.getCls(), ent);
		this.suf = suf;
	}

	public <T> ReferenceStructure(List<T> list, String suf, Class<T> cls) {
		ReferenceEntry<?> ent = ReferenceConverter.genEntryAssortedType(list, cls);
		map.put(ent.getCls(), ent);
		this.suf = suf;
	}

	public ReferenceStructure(ReferenceEntry<?> ent, String suf, Class<?> cls) {
		map.put(cls, ent);
		this.suf = suf;
	}

	@SuppressWarnings("unchecked")
	public <T> ReferenceEntry<T> castEntrySuppressWarnings(Object obj) {
		return (ReferenceEntry<T>)obj;
	}

	@SuppressWarnings("unchecked")
	public <T> ReferenceEntry<T> conv(Class<T> cls) {
		// ToDo: ここはString変換とrqdの読み取り以外の用途はないのかもしれない
		if(cnvCls != null) {
			// 今のところmagic word作成時専用
			if(!map.containsKey(cls) && cls.equals(String.class)) {
				ReferenceEntry<T> entDst = null;
				for(ReferenceEntry<?> entSrc : map.values()) {
					entDst = ReferenceConverter.convEnt2Ent(entSrc, cls, cnvCls);
					break;
				}
				map.put(cls, entDst);
			}

			if(map.containsKey(cls)) {
				return castEntrySuppressWarnings(map.get(cls));
			}
		}

		if(!map.containsKey(cls)) {
			if(vals == null && fis == null) {
				if(map.size() > 0) {
					if(map.containsKey(String.class)) {
						ReferenceEntry<String> entSrc = (ReferenceEntry<String>)map.get(String.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(Integer.class)) {
						ReferenceEntry<Integer> entSrc = (ReferenceEntry<Integer>)map.get(Integer.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(Long.class)) {
						ReferenceEntry<Long> entSrc = (ReferenceEntry<Long>)map.get(Long.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(BigInteger.class)) {
						ReferenceEntry<BigInteger> entSrc = (ReferenceEntry<BigInteger>)map.get(BigInteger.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(BigDecimal.class)) {
						ReferenceEntry<BigDecimal> entSrc = (ReferenceEntry<BigDecimal>)map.get(BigDecimal.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(Number.class)) {
						ReferenceEntry<Number> entSrc = (ReferenceEntry<Number>)map.get(Number.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(Boolean.class)) {
						ReferenceEntry<Boolean> entSrc = (ReferenceEntry<Boolean>)map.get(Boolean.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(OffsetDateTime.class)) {
						ReferenceEntry<OffsetDateTime> entSrc = (ReferenceEntry<OffsetDateTime>)map.get(OffsetDateTime.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(LocalDateTime.class)) {
						ReferenceEntry<LocalDateTime> entSrc = (ReferenceEntry<LocalDateTime>)map.get(LocalDateTime.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(LocalDate.class)) {
						ReferenceEntry<LocalDate> entSrc = (ReferenceEntry<LocalDate>)map.get(LocalDate.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(LocalTime.class)) {
						ReferenceEntry<LocalTime> entSrc = (ReferenceEntry<LocalTime>)map.get(LocalTime.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(Date.class)) {
						ReferenceEntry<Date> entSrc = (ReferenceEntry<Date>)map.get(Date.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else if(map.containsKey(Calendar.class)) {
						ReferenceEntry<Calendar> entSrc = (ReferenceEntry<Calendar>)map.get(Calendar.class);
						ReferenceEntry<T> entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
						map.put(cls, entDst);
					} else {
						ReferenceEntry<T> entDst = null;
						for(ReferenceEntry<?> entSrc : map.values()) {
							entDst = ReferenceConverter.convEnt2Ent(entSrc, cls);
							break;
						}
						map.put(cls, entDst);
					}
				} else {
					ReferenceEntry<T> ent = new ReferenceEntry<>(mnn, suf, cls);
					map.put(cls, ent);
				}
			} else if(vals != null) {
				ReferenceEntry<T> ent = new ReferenceEntry<>(vals, utf8Flg, cls);
				map.put(cls, ent);
			} else {
				ReferenceEntry<T> ent = new ReferenceEntry<>(fis, utf8Flg, cls);
				map.put(cls, ent);
			}
		}

		return castEntrySuppressWarnings(map.get(cls));
	}

	@SuppressWarnings("unchecked")
	public String getMnn() {
		if(mnn == null && map.size() > 0) {
			if(map.containsKey(String.class)) {
				ReferenceEntry<String> ent = (ReferenceEntry<String>)map.get(String.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(Integer.class)) {
				ReferenceEntry<Integer> ent = (ReferenceEntry<Integer>)map.get(Integer.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(Long.class)) {
				ReferenceEntry<Long> ent = (ReferenceEntry<Long>)map.get(Long.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(BigInteger.class)) {
				ReferenceEntry<BigInteger> ent = (ReferenceEntry<BigInteger>)map.get(BigInteger.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(BigDecimal.class)) {
				ReferenceEntry<BigDecimal> ent = (ReferenceEntry<BigDecimal>)map.get(BigDecimal.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(Number.class)) {
				ReferenceEntry<Number> ent = (ReferenceEntry<Number>)map.get(Number.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(Boolean.class)) {
				ReferenceEntry<Boolean> ent = (ReferenceEntry<Boolean>)map.get(Boolean.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(OffsetDateTime.class)) {
				ReferenceEntry<OffsetDateTime> ent = (ReferenceEntry<OffsetDateTime>)map.get(OffsetDateTime.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(LocalDateTime.class)) {
				ReferenceEntry<LocalDateTime> ent = (ReferenceEntry<LocalDateTime>)map.get(LocalDateTime.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(LocalDate.class)) {
				ReferenceEntry<LocalDate> ent = (ReferenceEntry<LocalDate>)map.get(LocalDate.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(LocalTime.class)) {
				ReferenceEntry<LocalTime> ent = (ReferenceEntry<LocalTime>)map.get(LocalTime.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(Date.class)) {
				ReferenceEntry<Date> ent = (ReferenceEntry<Date>)map.get(Date.class);
				mnn = ent.getMnn();
			} else if(map.containsKey(Calendar.class)) {
				ReferenceEntry<Calendar> ent = (ReferenceEntry<Calendar>)map.get(Calendar.class);
				mnn = ent.getMnn();
			} else {
				for(ReferenceEntry<?> ent : map.values()) {
					mnn = ent.getMnn();
				}
			}
		} else if(vals != null) {
			ReferenceEntry<String> ent = new ReferenceEntry<>(vals, utf8Flg, String.class);
			map.put(String.class, ent);
			mnn = ent.getMnn();
		} else if(fis != null) {
			ReferenceEntry<String> ent = new ReferenceEntry<>(fis, utf8Flg, String.class);
			map.put(String.class, ent);
			mnn = ent.getMnn();
		}

		return mnn;
	}

	public FileItem getFileItem() {
		if(fis == null) {
			for(ReferenceEntry<?> ent : map.values()) {
				if(ent.getIsFile()) {
					return ent.getFileItem();
				}
			}
		} else {
			return fis.get(0);
		}
		return null;
	}

	public List<FileItem> getFileItemList() {
		if(fis == null) {
			for(ReferenceEntry<?> ent : map.values()) {
				if(ent.getIsFile()) {
					return ent.getFileItemList();
				}
			}
		}
		return fis;
	}

	public boolean getIsFile() {
		return isFile;
	}

	public boolean getUtf8Flg() {
		return utf8Flg;
	}

	public Class<? extends Converter> getCnvCls() {
		return cnvCls;
	}

	public void setCnvCls(Class<? extends Converter> cnvCls) {
		this.cnvCls = cnvCls;
	}

	@Override
	public boolean equals(Object tgt) {
		if(tgt == null) {
			return false;
		} else if(tgt instanceof ReferenceStructure) {
			ReferenceStructure ref = (ReferenceStructure)tgt;
			return vals.equals(ref.vals); // ToDo: 再検討
		}
		return false;
	}

	public boolean isEmpty() {
		return (vals == null || vals.length == 0 || vals[0] == null || vals[0].length() == 0)
				&& (fis == null || fis.size() == 0 || fis.get(0) == null || fis.get(0).getSize() == 0);
	}

}

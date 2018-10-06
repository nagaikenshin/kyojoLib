package org.kyojo.core;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.converter.BooleanExConverter;
import org.kyojo.core.converter.DateExConverter;
import org.kyojo.core.converter.DateTimeExConverter;
import org.kyojo.core.converter.LocalDateExConverter;
import org.kyojo.core.converter.LocalDateTimeExConverter;
import org.kyojo.core.converter.LocalTimeExConverter;
import org.kyojo.core.converter.OffsetDateTimeExConverter;
import org.kyojo.core.converter.TimeExConverter;
import org.kyojo.gson.JsonParseException;
import org.kyojo.gson.JsonSyntaxException;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;
import org.kyojo.schemaorg.NativeValueDataType;
import org.kyojo.schemaorg.SimpleJsonBuilder;

public class ReferenceConverter {

	private static final Log logger = LogFactory.getLog(ReferenceConverter.class);

	// BeanUtilsのコードと同じ取得方法
	private static final ContextClassLoaderLocal<BeanUtilsBean> BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<BeanUtilsBean>() {
		@Override
		protected BeanUtilsBean initialValue() {
			ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
			BooleanExConverter booleanExConverter = new BooleanExConverter();
			convertUtilsBean.register(booleanExConverter, Boolean.class);
			DateTimeExConverter dateTimeExConverter = new DateTimeExConverter();
			convertUtilsBean.register(dateTimeExConverter, java.util.Date.class);
			DateExConverter dateExConverter = new DateExConverter();
			convertUtilsBean.register(dateExConverter, java.sql.Date.class);
			TimeExConverter timeExConverter = new TimeExConverter();
			convertUtilsBean.register(timeExConverter, java.sql.Time.class);
			OffsetDateTimeExConverter offsetDateTimeExConverter = new OffsetDateTimeExConverter();
			convertUtilsBean.register(offsetDateTimeExConverter, OffsetDateTime.class);
			LocalDateTimeExConverter localDateTimeExConverter = new LocalDateTimeExConverter();
			convertUtilsBean.register(localDateTimeExConverter, LocalDateTime.class);
			LocalDateExConverter localDateExConverter = new LocalDateExConverter();
			convertUtilsBean.register(localDateExConverter, LocalDate.class);
			LocalTimeExConverter localTimeExConverter = new LocalTimeExConverter();
			convertUtilsBean.register(localTimeExConverter, LocalTime.class);
			return new BeanUtilsBean(convertUtilsBean);
		}
	};

	public static BeanUtilsBean getBeanUtils() {
		return BEANS_BY_CLASSLOADER.get();
	}

	public static ConvertUtilsBean getConvertUtils() {
		return getBeanUtils().getConvertUtils();
	}

	private static ConcurrentHashMap<String, ContextClassLoaderLocal<BeanUtilsBean>> loaderMap = new ConcurrentHashMap<>();

	public static BeanUtilsBean getBeanUtils(Class<? extends Converter> cnvCls, Class<?> tgtCls) {
		String loaderKey = cnvCls.getName() + " " + tgtCls.getName();
		if(!loaderMap.containsKey(loaderKey)) {
			final ContextClassLoaderLocal<BeanUtilsBean> BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<BeanUtilsBean>() {
				@Override
				protected BeanUtilsBean initialValue() {
					ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
					try {
						Converter converter = cnvCls.newInstance();
						convertUtilsBean.register(converter, tgtCls);
						return new BeanUtilsBean(convertUtilsBean);
					} catch(InstantiationException ie) {
						logger.warn(ie.getMessage(), ie);
					} catch(IllegalAccessException iae) {
						logger.warn(iae.getMessage(), iae);
					}

					return null;
				}
			};
			loaderMap.put(loaderKey, BEANS_BY_CLASSLOADER);
		}

		ContextClassLoaderLocal<BeanUtilsBean> loader = loaderMap.get(loaderKey);
		return loader.get();
	}

	public static ConvertUtilsBean getConvertUtils(Class<? extends Converter> cnvCls, Class<?> tgtCls) {
		return getBeanUtils(cnvCls, tgtCls).getConvertUtils();
	}

	@SuppressWarnings("unchecked")
	public static <T> ReferenceEntry<?> genEntryAssortedType(Object obj, Class<T> cls,
			Class<? extends Converter> cnvCls) {
		if(cnvCls == null) {
			return genEntryAssortedType(obj, cls);
		}

		return new ReferenceEntry<T>((T)obj, cls);
	}

	@SuppressWarnings("unchecked")
	public static <T> ReferenceEntry<?> genEntryAssortedType(Object obj, Class<T> cls) {
		if(String.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<String>((String)obj);
		} else if(Integer.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Integer>((Integer)obj, Integer.class);
		} else if(Long.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Long>((Long)obj, Long.class);
		} else if(BigInteger.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<BigInteger>((BigInteger)obj, BigInteger.class);
		} else if(BigDecimal.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<BigDecimal>((BigDecimal)obj, BigDecimal.class);
		} else if(Number.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Number>((Number)obj, Number.class);
		} else if(Boolean.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Boolean>((Boolean)obj, Boolean.class);
		} else if(OffsetDateTime.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<OffsetDateTime>((OffsetDateTime)obj, OffsetDateTime.class);
		} else if(LocalDateTime.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<LocalDateTime>((LocalDateTime)obj, LocalDateTime.class);
		} else if(LocalDate.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<LocalDate>((LocalDate)obj, LocalDate.class);
		} else if(LocalTime.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<LocalTime>((LocalTime)obj, LocalTime.class);
		} else if(Date.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Date>((Date)obj, Date.class);
		} else if(Calendar.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Calendar>((Calendar)obj, Calendar.class);
		} else {
			return new ReferenceEntry<T>((T)obj, cls);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ReferenceEntry<?> genEntryAssortedType(List<?> list, Class<T> cls,
			Class<? extends Converter> cnvCls) {
		if(cnvCls == null) {
			return genEntryAssortedType(list, cls);
		}

		return new ReferenceEntry<T>((List<T>)list, cls);
	}

	@SuppressWarnings("unchecked")
	public static <T> ReferenceEntry<?> genEntryAssortedType(List<?> list, Class<T> cls) {
		if(String.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<String>((List<String>)list, String.class);
		} else if(Integer.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Integer>((List<Integer>)list, Integer.class);
		} else if(Long.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Long>((List<Long>)list, Long.class);
		} else if(BigInteger.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<BigInteger>((List<BigInteger>)list, BigInteger.class);
		} else if(BigDecimal.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<BigDecimal>((List<BigDecimal>)list, BigDecimal.class);
		} else if(Number.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Number>((List<Number>)list, Number.class);
		} else if(Boolean.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Boolean>((List<Boolean>)list, Boolean.class);
		} else if(OffsetDateTime.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<OffsetDateTime>((List<OffsetDateTime>)list, OffsetDateTime.class);
		} else if(LocalDateTime.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<LocalDateTime>((List<LocalDateTime>)list, LocalDateTime.class);
		} else if(LocalDate.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<LocalDate>((List<LocalDate>)list, LocalDate.class);
		} else if(LocalTime.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<LocalTime>((List<LocalTime>)list, LocalTime.class);
		} else if(Date.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Date>((List<Date>)list, Date.class);
		} else if(Calendar.class.isAssignableFrom(cls)) {
			return new ReferenceEntry<Calendar>((List<Calendar>)list, Calendar.class);
		} else {
			return new ReferenceEntry<T>((List<T>)list, cls);
		}
	}

	public static <T> ReferenceStructure genStructureAssortedType(Object obj, String suf, Class<T> cls) {
		ReferenceEntry<?> ent = ReferenceConverter.genEntryAssortedType(obj, cls);
		ReferenceStructure ref = new ReferenceStructure(ent, suf, ent.getCls());
		return ref;
	}

	public static <T> ReferenceStructure genStructureAssortedType(Object obj, String suf, Class<T> cls,
			Class<? extends Converter> cnvCls) {
		ReferenceEntry<?> ent = ReferenceConverter.genEntryAssortedType(obj, cls, cnvCls);
		ReferenceStructure ref = new ReferenceStructure(ent, suf, ent.getCls());
		ref.setCnvCls(cnvCls);
		return ref;
	}

	public static <T> ReferenceStructure genStructureAssortedType(List<?> list, String suf, Class<T> cls) {
		ReferenceEntry<?> ent = ReferenceConverter.genEntryAssortedType(list, cls);
		ReferenceStructure ref = new ReferenceStructure(ent, suf, ent.getCls());
		return ref;
	}

	public static <T> ReferenceStructure genStructureAssortedType(List<?> list, String suf, Class<T> cls,
			Class<? extends Converter> cnvCls) {
		ReferenceEntry<?> ent = ReferenceConverter.genEntryAssortedType(list, cls, cnvCls);
		ReferenceStructure ref = new ReferenceStructure(ent, suf, ent.getCls());
		ref.setCnvCls(cnvCls);
		return ref;
	}

	public static <T> String convCls2Suf(Class<T> cls) {
		if(String.class.isAssignableFrom(cls) || cls.isPrimitive()
				|| Number.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls)
				|| Date.class.isAssignableFrom(cls) || Calendar.class.isAssignableFrom(cls)
				|| OffsetDateTime.class.isAssignableFrom(cls) || LocalDateTime.class.isAssignableFrom(cls)
				|| LocalDate.class.isAssignableFrom(cls) || LocalTime.class.isAssignableFrom(cls)) {
			return "";
		} else {
			NativeValueDataType nvdt = NativeValueDataType.getEnumByDataTypeClass(cls);
			if(nvdt == null) {
				return Constants.MINION_SUFFIX;
			} else {
				return "";
			}
		}
	}

	public static String convKey2Suf(String key) {
		if(key != null && key.endsWith(My.constantize("." + Constants.MINION_SUFFIX))) {
			return Constants.MINION_SUFFIX;
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	public static <T> T convVal2Obj(String val, Class<T> cls) {
		if(StringUtils.isBlank(val) && Number.class.isAssignableFrom(cls)) {
			return null;
		} else if(String.class.isAssignableFrom(cls) || cls.isPrimitive()
				|| Number.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls)
				|| Date.class.isAssignableFrom(cls) || Calendar.class.isAssignableFrom(cls)
				|| OffsetDateTime.class.isAssignableFrom(cls) || LocalDateTime.class.isAssignableFrom(cls)
				|| LocalDate.class.isAssignableFrom(cls) || LocalTime.class.isAssignableFrom(cls)) {
			// ToDo: デフォルト値とかもっとちゃんと
			try {
				return (T)getConvertUtils().convert(val, cls);
			} catch(ConversionException ce) {
				logger.info(ce.getMessage());
				return null;
			}
		} else {
			String str = "\"" + SimpleJsonBuilder.escapeJson(val) + "\"";
			try {
				return My.deminion(str, cls);
			} catch(JsonSyntaxException jse) {
				logger.warn("JsonSyntaxException: " + jse.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(str, 1024));
				logger.warn("class: " + cls.getName());
				return null;
			} catch(JsonParseException jpe) {
				logger.warn("JsonParseException: " + jpe.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(str, 1024));
				logger.warn("class: " + cls.getName());
				return null;
			} catch(Exception ex) {
				logger.warn("Exception: " + ex.getMessage());
				logger.warn("json: " + StringUtils.abbreviate(str, 1024));
				logger.warn("class: " + cls.getName());
				return null;
			}
		}
	}

	public static <T> String convObj2Val(T obj, Class<T> cls) {
		if(obj == null) {
			return "";
		}

		String val = null;
		if(OffsetDateTime.class.isAssignableFrom(cls)) {
			DateTimeFormatter ymdhmszDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
			OffsetDateTime odt = (OffsetDateTime)obj;
			val = odt.format(ymdhmszDtf);
		} else if(LocalDateTime.class.isAssignableFrom(cls)) {
			DateTimeFormatter ymdhmsDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			LocalDateTime ldt = (LocalDateTime)obj;
			val = ldt.format(ymdhmsDtf);
		} else if(LocalDate.class.isAssignableFrom(cls)) {
			DateTimeFormatter ymdDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate ld = (LocalDate)obj;
			val = ld.format(ymdDtf);
		} else if(LocalTime.class.isAssignableFrom(cls)) {
			DateTimeFormatter hmsDtf = DateTimeFormatter.ofPattern("HH:mm:ss");
			LocalTime lt = (LocalTime)obj;
			val = lt.format(hmsDtf);
		} else if(Date.class.isAssignableFrom(cls)) {
			SimpleDateFormat ymdhmsSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			val = ymdhmsSdf.format((Date)obj);
		} else if(Calendar.class.isAssignableFrom(cls)) {
			SimpleDateFormat ymdhmszSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			Calendar cal = (Calendar)obj;
			ymdhmszSdf.setTimeZone(cal.getTimeZone());
			val = ymdhmszSdf.format(cal.getTime());
		} else if(String.class.isAssignableFrom(cls)
				|| Number.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls)) {
			val = obj.toString();
		} else {
			val = SimpleJsonBuilder.toJson(obj, cls);
		}

		return val;
	}

	@SuppressWarnings("unchecked")
	public static <T> ReferenceEntry<T> convEnt2Ent(ReferenceEntry<?> entSrc, Class<T> clsDst,
			Class<? extends Converter> cnvCls) {
		if(entSrc.getIsList()) {
			List<?> lstSrc = entSrc.getList();
			if(lstSrc == null) {
				return new ReferenceEntry<T>((List<T>)null, clsDst);
			}
			List<T> lstDst = new ArrayList<>();
			for(Object valSrc : lstSrc) {
				try {
					T valDst = (T)getConvertUtils(cnvCls, valSrc.getClass()).convert(valSrc, clsDst);
					lstDst.add(valDst);
				} catch(ConversionException ce) {
					logger.info(ce.getMessage());
					lstDst.add(null);
				}
			}
			ReferenceEntry<T> entDst = new ReferenceEntry<>(lstDst, clsDst);
			return entDst;
		} else {
			Object valSrc = entSrc.getObj();
			T valDst = null;
			try {
				valDst = (T)getConvertUtils(cnvCls, valSrc.getClass()).convert(valSrc, clsDst);
			} catch(ConversionException ce) {
				logger.info(ce.getMessage());
			}
			ReferenceEntry<T> entDst = new ReferenceEntry<>(valDst, clsDst);
			return entDst;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ReferenceEntry<T> convEnt2Ent(ReferenceEntry<?> entSrc, Class<T> clsDst) {
		Class<?> clsSrc = entSrc.getCls();
		if(clsDst.isAssignableFrom(clsSrc)) {
			if(entSrc.getIsList()) {
				List<?> lstSrc = entSrc.getList();
				List<T> lstDst = (List<T>)lstSrc;
				ReferenceEntry<T> entDst = new ReferenceEntry<>(lstDst, clsDst);
				return entDst;
			} else {
				Object valSrc = entSrc.getObj();
				T valDst = (T)valSrc;
				ReferenceEntry<T> entDst = new ReferenceEntry<>(valDst, clsDst);
				return entDst;
			}
		} else if((String.class.isAssignableFrom(clsSrc) || clsSrc.isPrimitive()
					|| Number.class.isAssignableFrom(clsSrc) || Boolean.class.isAssignableFrom(clsSrc)
					|| Date.class.isAssignableFrom(clsSrc) || Calendar.class.isAssignableFrom(clsSrc)
					|| OffsetDateTime.class.isAssignableFrom(clsSrc) || LocalDateTime.class.isAssignableFrom(clsSrc)
					|| LocalDate.class.isAssignableFrom(clsSrc) || LocalTime.class.isAssignableFrom(clsSrc))
				&& (String.class.isAssignableFrom(clsDst) || clsDst.isPrimitive()
					|| Number.class.isAssignableFrom(clsDst) || Boolean.class.isAssignableFrom(clsDst)
					|| Date.class.isAssignableFrom(clsDst) || Calendar.class.isAssignableFrom(clsDst)
					|| OffsetDateTime.class.isAssignableFrom(clsDst) || LocalDateTime.class.isAssignableFrom(clsDst)
					|| LocalDate.class.isAssignableFrom(clsDst) || LocalTime.class.isAssignableFrom(clsDst))) {
			if(entSrc.getIsList()) {
				List<?> lstSrc = entSrc.getList();
				if(lstSrc == null) {
					return new ReferenceEntry<T>((List<T>)null, clsDst);
				}
				List<T> lstDst = new ArrayList<>();
				for(Object valSrc : lstSrc) {
					try {
						T valDst = (T)getConvertUtils().convert(valSrc, clsDst);
						lstDst.add(valDst);
					} catch(ConversionException ce) {
						logger.info(ce.getMessage());
						lstDst.add(null);
					}
				}
				ReferenceEntry<T> entDst = new ReferenceEntry<>(lstDst, clsDst);
				return entDst;
			} else {
				Object valSrc = entSrc.getObj();
				T valDst = null;
				try {
					valDst = (T)getConvertUtils().convert(valSrc, clsDst);
				} catch(ConversionException ce) {
					logger.info(ce.getMessage());
				}
				ReferenceEntry<T> entDst = new ReferenceEntry<>(valDst, clsDst);
				return entDst;
			}
		} else {
			NativeValueDataType nvdt = NativeValueDataType.getEnumByDataTypeClass(clsSrc);
			if(nvdt != null && String.class.isAssignableFrom(clsDst)) {
				if(entSrc.getIsList()) {
					List<?> lstSrc = entSrc.getList();
					if(lstSrc == null) {
						return new ReferenceEntry<T>((List<T>)null, clsDst);
					}
					List<T> lstDst = new ArrayList<>();
					for(Object valSrc : lstSrc) {
						T valDst = (T)NativeValueDataType.dataTypeToString(valSrc, nvdt);
						lstDst.add(valDst);
					}
					ReferenceEntry<T> entDst = new ReferenceEntry<>(lstDst, clsDst);
					return entDst;
				} else {
					Object valSrc = entSrc.getObj();
					T valDst = (T)NativeValueDataType.dataTypeToString(valSrc, nvdt);
					ReferenceEntry<T> entDst = new ReferenceEntry<>(valDst, clsDst);
					return entDst;
				}
			} else {
				String mnnSrc = entSrc.getMnn();
				if(entSrc.getIsList()) {
					try {
						Type listType = TypeToken.getParameterized(List.class, clsDst).getType();
						List<T> lstDst = My.deminion(mnnSrc, listType);
						ReferenceEntry<T> entDst = new ReferenceEntry<>(lstDst, clsDst);
						return entDst;
					} catch(JsonSyntaxException jse) {
						logger.warn("JsonSyntaxException: " + jse.getMessage());
						logger.warn("json: " + StringUtils.abbreviate(mnnSrc, 1024));
						logger.warn("class: " + clsDst.getName());
						return null;
					} catch(JsonParseException jpe) {
						logger.warn("JsonParseException: " + jpe.getMessage());
						logger.warn("json: " + StringUtils.abbreviate(mnnSrc, 1024));
						logger.warn("class: " + clsDst.getName());
						return null;
					} catch(Exception ex) {
						logger.warn("Exception: " + ex.getMessage());
						logger.warn("json: " + StringUtils.abbreviate(mnnSrc, 1024));
						logger.warn("class: " + clsDst.getName());
						return null;
					}
				} else {
					try {
						T objDst = My.deminion(mnnSrc, clsDst);
						ReferenceEntry<T> entDst = new ReferenceEntry<>(objDst, clsDst);
						return entDst;
					} catch(JsonSyntaxException jse) {
						logger.warn("JsonSyntaxException: " + jse.getMessage());
						logger.warn("json: " + StringUtils.abbreviate(mnnSrc, 1024));
						logger.warn("class: " + clsDst.getName());
						return null;
					} catch(JsonParseException jpe) {
						logger.warn("JsonParseException: " + jpe.getMessage());
						logger.warn("json: " + StringUtils.abbreviate(mnnSrc, 1024));
						logger.warn("class: " + clsDst.getName());
						return null;
					} catch(Exception ex) {
						logger.warn("Exception: " + ex.getMessage());
						logger.warn("json: " + StringUtils.abbreviate(mnnSrc, 1024));
						logger.warn("class: " + clsDst.getName());
						return null;
					}
				}
			}
		}
	}

}

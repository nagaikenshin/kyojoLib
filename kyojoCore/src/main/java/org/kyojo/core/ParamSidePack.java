package org.kyojo.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.kyojo.core.annotation.SpecifiedConverter;

public class ParamSidePack {

	private ReferenceStructure cnvRef;
	private Class<? extends Converter> cnvCls;
	// private Method[] mtds;
	// private Field fld;
	private String vldMsg;

	public ReferenceStructure getConversionReference() {
		return cnvRef;
	}

	public static boolean hasConversionReference(ParamSidePack psp) {
		return psp != null && psp.cnvRef != null && !psp.cnvRef.isEmpty();
	}

	public Class<? extends Converter> getConversionClass() {
		return cnvCls;
	}

	public String getValidationMessage() {
		return vldMsg;
	}

	public static ParamSidePack byConversionClass(Method[] mtds, Field fld, String key, ResponseData rpd) {
		ParamSidePack psp = rpd.get(key);
		Method sm = mtds[1];

		// converterの取得
		if(sm.isAnnotationPresent(SpecifiedConverter.class)) {
			if(psp == null) {
				psp = new ParamSidePack();
			}
			// psp.mtds = mtds;
			psp.cnvCls = sm.getAnnotation(SpecifiedConverter.class).value();
			rpd.put(key, psp);
		} else if(fld != null) {
			psp = byConversionClass(fld, key, rpd);
		}

		return psp;
	}

	public static ParamSidePack byConversionClass(Field fld, String key, ResponseData rpd) {
		ParamSidePack psp = rpd.get(key);

		// converterの取得
		if(fld.isAnnotationPresent(SpecifiedConverter.class)) {
			if(psp == null) {
				psp = new ParamSidePack();
			}
			// psp.fld = fld;
			psp.cnvCls = fld.getAnnotation(SpecifiedConverter.class).value();
			rpd.put(key, psp);
		}

		return psp;
	}

	public static ParamSidePack byParent(ParamSidePack prt, String key, ResponseData rpd) {
		ParamSidePack psp = rpd.get(key);

		if(prt != null && prt.cnvCls != null) {
			if(psp == null) {
				psp = new ParamSidePack();
			}

			psp.cnvCls = prt.cnvCls;
			rpd.put(key, psp);
		}

		return psp;
	}

	public static boolean hasConversionClass(ParamSidePack psp) {
		return psp != null && psp.cnvCls != null;
	}

	public static ParamSidePack byConversionReference(ReferenceStructure cnvRef, ParamSidePack prt,
			String key, ResponseData rpd) {
		ParamSidePack psp = rpd.get(key);

		if(cnvRef != null) {
			if(psp == null) {
				psp = new ParamSidePack();
			}
			if(prt == null) {
				psp.cnvCls = null;
			} else {
				psp.cnvCls = prt.cnvCls;
			}
			psp.cnvRef = cnvRef;
			rpd.put(key, psp);
		}

		return psp;
	}

	public static ParamSidePack byValidationMessage(String vldMsg,
			String key, ResponseData rpd) {
		ParamSidePack psp = rpd.get(key);

		if(StringUtils.isNotBlank(vldMsg)) {
			if(psp == null) {
				psp = new ParamSidePack();
			}
			psp.vldMsg = vldMsg;
			rpd.put(key, psp);
		}

		return psp;
	}

	public static boolean hasValidationMessage(ParamSidePack psp) {
		return psp != null && psp.vldMsg != null;
	}

}

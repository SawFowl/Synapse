package sawfowl.synapse.api;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The list of locales available in Vanilla Minecraft.
 */
public final class Locales {

	public static final Locale AF_ZA = Locale.of("af", "ZA");
	public static final Locale AR_SA = Locale.of("ar", "SA");
	public static final Locale AST_ES = Locale.of("ast", "ES");
	public static final Locale AZ_AZ = Locale.of("az", "AZ");
	public static final Locale BG_BG = Locale.of("bg", "BG");
	public static final Locale CA_ES = Locale.of("ca", "ES");
	public static final Locale CS_CZ = Locale.of("cs", "CZ");
	public static final Locale CY_GB = Locale.of("cy", "GB");
	public static final Locale DA_DK = Locale.of("da", "DK");
	public static final Locale DE_DE = Locale.GERMANY;
	public static final Locale EL_GR = Locale.of("el", "GR");
	public static final Locale EN_AU = Locale.of("en", "AU");
	public static final Locale EN_CA = Locale.of("en", "CA");
	public static final Locale EN_GB = Locale.UK;
	public static final Locale EN_PT = Locale.of("en", "PT");
	public static final Locale EN_US = Locale.US;
	public static final Locale EO_UY = Locale.of("eo", "UY");
	public static final Locale ES_AR = Locale.of("es", "AR");
	public static final Locale ES_ES = Locale.of("es", "ES");
	public static final Locale ES_MX = Locale.of("es", "MX");
	public static final Locale ES_UY = Locale.of("es", "UY");
	public static final Locale ES_VE = Locale.of("es", "VE");
	public static final Locale ET_EE = Locale.of("et", "EE");
	public static final Locale EU_ES = Locale.of("eu", "ES");
	public static final Locale FA_IR = Locale.of("fa", "IR");
	public static final Locale FI_FI = Locale.of("fi", "FI");
	public static final Locale FIL_PH = Locale.of("fil", "PH");
	public static final Locale FR_CA = Locale.CANADA_FRENCH;
	public static final Locale FR_FR = Locale.FRANCE;
	public static final Locale GA_IE = Locale.of("ga", "IE");
	public static final Locale GL_ES = Locale.of("gl", "ES");
	public static final Locale GV_IM = Locale.of("gv", "IM");
	public static final Locale HE_IL = Locale.of("he", "IL");
	public static final Locale HI_IN = Locale.of("hi", "IN");
	public static final Locale HR_HR = Locale.of("hr", "HR");
	public static final Locale HU_HU = Locale.of("hu", "HU");
	public static final Locale HY_AM = Locale.of("hy", "AM");
	public static final Locale ID_ID = Locale.of("id", "ID");
	public static final Locale IS_IS = Locale.of("is", "IS");
	public static final Locale IT_IT = Locale.ITALY;
	public static final Locale JA_JP = Locale.JAPAN;
	public static final Locale KA_GE = Locale.of("ka", "GE");
	public static final Locale KO_KR = Locale.KOREA;
	public static final Locale KW_GB = Locale.of("kw", "GB");
	public static final Locale LA_LA = Locale.of("la", "LA");
	public static final Locale LB_LU = Locale.of("lb", "LU");
	public static final Locale LT_LT = Locale.of("lt", "LT");
	public static final Locale LV_LV = Locale.of("lv", "LV");
	public static final Locale MI_NZ = Locale.of("mi", "NZ");
	public static final Locale MS_MY = Locale.of("ms", "MY");
	public static final Locale MT_MT = Locale.of("mt", "MT");
	public static final Locale NDS_DE = Locale.of("nds", "DE");
	public static final Locale NL_NL = Locale.of("nl", "NL");
	public static final Locale NN_NO = Locale.of("nn", "NO");
	public static final Locale NO_NO = Locale.of("no", "NO");
	public static final Locale OC_FR = Locale.of("oc", "FR");
	public static final Locale PL_PL = Locale.of("pl", "PL");
	public static final Locale PT_BR = Locale.of("pt", "BR");
	public static final Locale PT_PT = Locale.of("pt", "PT");
	public static final Locale QYA_AA = Locale.of("qya", "AA");
	public static final Locale RO_RO = Locale.of("ro", "RO");
	public static final Locale RU_RU = Locale.of("ru", "RU");
	public static final Locale SE_NO = Locale.of("se", "NO");
	public static final Locale SK_SK = Locale.of("sk", "SK");
	public static final Locale SL_SI = Locale.of("sl", "SI");
	public static final Locale SR_SP = Locale.of("sr", "SP");
	public static final Locale SV_SE = Locale.of("sv", "SE");
	public static final Locale TH_TH = Locale.of("th", "TH");
	public static final Locale TLH_AA = Locale.of("tlh", "AA");
	public static final Locale TR_TR = Locale.of("tr", "TR");
	public static final Locale UK_UA = Locale.of("uk", "UA");
	public static final Locale VAL_ES = Locale.of("val", "ES");
	public static final Locale VI_VN = Locale.of("vi", "VN");
	public static final Locale ZH_CN = Locale.SIMPLIFIED_CHINESE;
	public static final Locale ZH_TW = Locale.TRADITIONAL_CHINESE;

	/**
	 * The default locale used when the receiver's locale is unknown.
	 */
	public static final Locale DEFAULT = Locales.EN_US;

	// Suppress default constructor to ensure non-instantiability.
	private Locales() {
		throw new AssertionError("You should not be attempting to instantiate this class.");
	}

	public enum EnumLocales {

		AF_ZA {

			@Override
			public Locale get() {
				return Locales.AF_ZA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		AR_SA {

			@Override
			public Locale get() {
				return Locales.AR_SA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		AST_ES {

			@Override
			public Locale get() {
				return Locales.AST_ES;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		AZ_AZ {

			@Override
			public Locale get() {
				return Locales.AZ_AZ;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		BG_BG {

			@Override
			public Locale get() {
				return Locales.BG_BG;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		CA_ES {

			@Override
			public Locale get() {
				return Locales.CA_ES;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		CS_CZ {

			@Override
			public Locale get() {
				return Locales.CS_CZ;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		CY_GB {

			@Override
			public Locale get() {
				return Locales.CY_GB;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		DA_DK {

			@Override
			public Locale get() {
				return Locales.DA_DK;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		DE_DE {

			@Override
			public Locale get() {
				return Locales.DE_DE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EL_GR {

			@Override
			public Locale get() {
				return Locales.EL_GR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EN_AU {

			@Override
			public Locale get() {
				return Locales.EN_AU;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EN_CA {

			@Override
			public Locale get() {
				return Locales.EN_CA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EN_GB {

			@Override
			public Locale get() {
				return Locales.EN_GB;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EN_PT {

			@Override
			public Locale get() {
				return Locales.EN_PT;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EN_US {

			@Override
			public Locale get() {
				return Locales.EN_US;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EO_UY {

			@Override
			public Locale get() {
				return Locales.EO_UY;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ES_AR {

			@Override
			public Locale get() {
				return Locales.ES_AR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ES_ES {

			@Override
			public Locale get() {
				return Locales.ES_ES;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ES_MX {

			@Override
			public Locale get() {
				return Locales.ES_MX;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ES_UY {

			@Override
			public Locale get() {
				return Locales.ES_UY;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ES_VE {

			@Override
			public Locale get() {
				return Locales.ES_VE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ET_EE {

			@Override
			public Locale get() {
				return Locales.ET_EE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		EU_ES {

			@Override
			public Locale get() {
				return Locales.EU_ES;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		FA_IR {

			@Override
			public Locale get() {
				return Locales.FA_IR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		FI_FI {

			@Override
			public Locale get() {
				return Locales.FI_FI;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		FIL_PH {

			@Override
			public Locale get() {
				return Locales.FIL_PH;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		FR_CA {

			@Override
			public Locale get() {
				return Locales.FR_CA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		FR_FR {

			@Override
			public Locale get() {
				return Locales.FR_FR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		GA_IE {

			@Override
			public Locale get() {
				return Locales.GA_IE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		GL_ES {

			@Override
			public Locale get() {
				return Locales.GL_ES;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		GV_IM {

			@Override
			public Locale get() {
				return Locales.GV_IM;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		HE_IL {

			@Override
			public Locale get() {
				return Locales.HE_IL;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		HI_IN {

			@Override
			public Locale get() {
				return Locales.HI_IN;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		HR_HR {

			@Override
			public Locale get() {
				return Locales.HR_HR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		HU_HU {

			@Override
			public Locale get() {
				return Locales.HU_HU;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		HY_AM {

			@Override
			public Locale get() {
				return Locales.HY_AM;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ID_ID {

			@Override
			public Locale get() {
				return Locales.ID_ID;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		IS_IS {

			@Override
			public Locale get() {
				return Locales.IS_IS;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		IT_IT {

			@Override
			public Locale get() {
				return Locales.IT_IT;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		JA_JP {

			@Override
			public Locale get() {
				return Locales.JA_JP;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		KA_GE {

			@Override
			public Locale get() {
				return Locales.KA_GE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		KO_KR {

			@Override
			public Locale get() {
				return Locales.KO_KR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		KW_GB {

			@Override
			public Locale get() {
				return Locales.KW_GB;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		LA_LA {

			@Override
			public Locale get() {
				return Locales.LA_LA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		LB_LU {

			@Override
			public Locale get() {
				return Locales.LB_LU;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		LT_LT {

			@Override
			public Locale get() {
				return Locales.LT_LT;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		LV_LV {

			@Override
			public Locale get() {
				return Locales.LV_LV;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		MI_NZ {

			@Override
			public Locale get() {
				return Locales.MI_NZ;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		MS_MY {

			@Override
			public Locale get() {
				return Locales.MS_MY;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		MT_MT {

			@Override
			public Locale get() {
				return Locales.MT_MT;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		NDS_DE {

			@Override
			public Locale get() {
				return Locales.NDS_DE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		NL_NL {

			@Override
			public Locale get() {
				return Locales.NL_NL;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		NN_NO {

			@Override
			public Locale get() {
				return Locales.NN_NO;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		NO_NO {

			@Override
			public Locale get() {
				return Locales.NO_NO;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		OC_FR {

			@Override
			public Locale get() {
				return Locales.OC_FR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		PL_PL {

			@Override
			public Locale get() {
				return Locales.PL_PL;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		PT_BR {

			@Override
			public Locale get() {
				return Locales.PT_BR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		PT_PT {

			@Override
			public Locale get() {
				return Locales.PT_PT;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		QYA_AA {

			@Override
			public Locale get() {
				return Locales.QYA_AA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		RO_RO {

			@Override
			public Locale get() {
				return Locales.RO_RO;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		RU_RU {

			@Override
			public Locale get() {
				return Locales.RU_RU;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		SE_NO {

			@Override
			public Locale get() {
				return Locales.SE_NO;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		SK_SK {

			@Override
			public Locale get() {
				return Locales.SK_SK;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		SL_SI {

			@Override
			public Locale get() {
				return Locales.SL_SI;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		SR_SP {

			@Override
			public Locale get() {
				return Locales.SR_SP;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		SV_SE {

			@Override
			public Locale get() {
				return Locales.SV_SE;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		TH_TH {

			@Override
			public Locale get() {
				return Locales.TH_TH;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		TLH_AA {

			@Override
			public Locale get() {
				return Locales.TLH_AA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		TR_TR {

			@Override
			public Locale get() {
				return Locales.TR_TR;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		UK_UA {

			@Override
			public Locale get() {
				return Locales.UK_UA;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		VAL_ES {

			@Override
			public Locale get() {
				return Locales.VAL_ES;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		VI_VN {

			@Override
			public Locale get() {
				return Locales.VI_VN;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ZH_CN {

			@Override
			public Locale get() {
				return Locales.ZH_CN;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		},
		ZH_TW {

			@Override
			public Locale get() {
				return Locales.ZH_TW;
			}

			@Override
			public String getTag() {
				return get().toLanguageTag();
			}

			@Override
			public String getCountry() {
				return get().getCountry();
			}

			@Override
			public String getDispayName() {
				return get().getDisplayName();
			}
			
		};
		
		public abstract Locale get();
		
		public static Locale getDefault() {
			return Locales.DEFAULT;
		}

		public abstract String getTag();

		public abstract String getCountry();

		public abstract String getDispayName();

		public static List<Locale> getLocales() {
			return Stream.of(EnumLocales.values()).map(EnumLocales::get).collect(Collectors.toList());
		}

		public static List<String> getTags() {
			return Stream.of(EnumLocales.values()).map(EnumLocales::getTag).collect(Collectors.toList());
		}

		public static List<String> getCountries() {
			return Stream.of(EnumLocales.values()).map(EnumLocales::getCountry).collect(Collectors.toList());
		}

		public static List<String> getDispayNames() {
			return Stream.of(EnumLocales.values()).map(EnumLocales::getDispayName).collect(Collectors.toList());
		}

		public static boolean exist(String locale) {
			return Stream.of(EnumLocales.values()).filter(value -> value.getTag().equals(locale)).findFirst().isPresent();
		}

		public static Locale find(String locale) {
			return Stream.of(EnumLocales.values()).filter(value -> value.getTag().equals(locale)).findFirst().map(l -> l.get()).orElse(getDefault());
		}

		public static boolean isValisTag(String tag) {
			return Stream.of(EnumLocales.values()).filter(value -> value.getTag().equals(tag)).findFirst().isPresent();
		}

	}

}

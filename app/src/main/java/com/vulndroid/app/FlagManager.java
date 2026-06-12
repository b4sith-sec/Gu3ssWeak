package com.vulndroid.app;

public class FlagManager {
    public static final String FLAG_WV_01    = "Gu3ssWeak{js_3nabl3d_n0_0r1g1n_WV01}";
    public static final String FLAG_WV_02    = "Gu3ssWeak{js_br1dg3_t0k3n_st3al_WV02}";
    public static final String FLAG_WV_03    = "Gu3ssWeak{f1l3_r3ad_v1a_w3bv13w_WV03}";
    public static final String FLAG_WV_04    = "Gu3ssWeak{arb_url_fr0m_1nt3nt_WV04}";
    public static final String FLAG_DL_02    = "Gu3ssWeak{arb_act1v1ty_d33pl1nk_DL02}";
    public static final String FLAG_DL_03    = "Gu3ssWeak{t0k3n_0v3rwr1t3_DL03}";
    public static final String FLAG_DL_CHAIN = "Gu3ssWeak{d33pl1nk_rce_ch41n_DLCHAIN}";
    public static final String FLAG_AP_01    = "Gu3ssWeak{exp0rt3d_adm1n_n0_p3rm_AP01}";
    public static final String FLAG_AP_02    = "Gu3ssWeak{h4rdc0d3d_s3cr3ts_AP02}";
    public static final String FLAG_BR_02    = "Gu3ssWeak{t0k3n_1nj3ct_br0adcast_BR02}";
    public static final String FLAG_BR_03    = "Gu3ssWeak{s3ns1t1v3_d4ta_l0gcat_BR03}";
    public static final String FLAG_SV_02A   = "Gu3ssWeak{d4ta_w1p3_s3rv1c3_SV02A}";
    public static final String FLAG_SV_02B   = "Gu3ssWeak{d4ta_3xf1l_ssrf_SV02B}";
    public static final String FLAG_NET_01 = "Gu3ssWeak{cl3artext_traff1c_n0_p1nn1ng_NET01}";
    public static final String FLAG_SQL_01 = "Gu3ssWeak{sql_1nj3ct10n_adm1n_byp4ss_SQL01}";
    public static final String FLAG_OTP_01 = "Gu3ssWeak{0tp_brut3f0rc3_n0_r4t3l1m1t_OTP01}";
    public static final String FLAG_FP_01 = "Gu3ssWeak{p4th_trav3rs4l_f1l3_r3ad_FP01}";
    public static final String FLAG_STORE_01 = "Gu3ssWeak{pl41nt3xt_sh4r3dpr3fs_STORE01}";
    public static final String FLAG_MASTER   = "Gu3ssWeak{y0u_0wn3d_th3_wh0l3_4pp_GG}";
    public static final String[] ALL_FLAGS   = {
        FLAG_WV_01, FLAG_WV_02, FLAG_WV_03, FLAG_WV_04,
        FLAG_DL_02, FLAG_DL_03, FLAG_DL_CHAIN,
        FLAG_AP_01, FLAG_AP_02,
        FLAG_BR_02, FLAG_BR_03,
        FLAG_SV_02A, FLAG_SV_02B, FLAG_SQL_01, FLAG_NET_01, FLAG_OTP_01, FLAG_FP_01, FLAG_STORE_01
    };
    public static void capture(android.content.Context ctx, String flag) {
        ctx.getSharedPreferences("ctf_flags", 0).edit().putBoolean(flag, true).apply();
        android.util.Log.d("Gu3ssWeak_FLAG", "FLAG: " + flag);
        android.widget.Toast.makeText(ctx, "FLAG CAPTURED: " + flag, android.widget.Toast.LENGTH_LONG).show();
    }
    public static boolean isCaptured(android.content.Context ctx, String flag) {
        return ctx.getSharedPreferences("ctf_flags", 0).getBoolean(flag, false);
    }
    public static int count(android.content.Context ctx) {
        int n = 0;
        for (String f : ALL_FLAGS) if (isCaptured(ctx, f)) n++;
        return n;
    }
}

import twitter4j.*;
import twitter4j.api.HelpResources.Language;
import twitter4j.auth.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

class textCleaner {

	static String cleanText, tweetText, hashTag, mention, tweetUrl, tweetEmoticons;
	String consecutiveDots = "";

	public textCleaner(String t) {
		tweetText = t;
		cleanText = t + " ";
		cleanText = cleanText.replace(' ', ' ');
		getEmoticons();
		cleanText = cleanText.replaceAll("[\\\'\"(){}\n?\\[\\]]", " ");
		cleanText = cleanText.replaceAll("[\\.]{2}", " ");
		getTweetUrls();
		getHashTags();
		getMentions();
		cleanText = cleanText.replaceAll("\\p{P}", " ");
		cleanText = "\"" + cleanText.substring(0, cleanText.length() - 1) + "\"";
	}

	public void getHashTags() {
		int countHashTags = cleanText.length() - cleanText.replace("#", "").length();
		String[] hashTags = new String[countHashTags];
		for (int i = 0; i < countHashTags; i++) {
			hashTags[i] = cleanText.substring(cleanText.indexOf('#', 0) + 1,
					cleanText.indexOf(' ', cleanText.indexOf('#', 0)));
			cleanText = cleanText.replaceFirst("#" + hashTags[i], "");
		}
		if (countHashTags == 0) {
			hashTag = "\"NULL\"";
		} else if (countHashTags == 1) {
			hashTag = "\"" + hashTags[0] + "\"";
		} else if (countHashTags > 1) {
			hashTag = "[";
			for (int i = 0; i < countHashTags; i++) {
				if (i == countHashTags - 1)
					hashTag = hashTag.concat("\"" + hashTags[i] + "\"");

				else
					hashTag = hashTag.concat("\"" + hashTags[i] + "\",");
			}
			hashTag = hashTag.concat("]");
		}
	}

	public void getMentions() {
		int countMentions = cleanText.length() - cleanText.replace("@", "").length();
		String[] mentions = new String[countMentions];
		for (int i = 0; i < countMentions; i++) {
			mentions[i] = cleanText.substring(cleanText.indexOf('@', 0) + 1,
					cleanText.indexOf(' ', cleanText.indexOf('@', 0)));
			cleanText = cleanText.replaceFirst("@" + mentions[i], "");
		}
		if (countMentions == 0) {
			mention = "\"NULL\"";
		} else if (countMentions == 1) {
			mention = "\"" + mentions[0] + "\"";
		} else if (countMentions > 1) {
			mention = "[";
			for (int i = 0; i < countMentions; i++) {
				if (i == countMentions - 1)
					mention = mention.concat("\"" + mentions[i] + "\"");

				else
					mention = mention.concat("\"" + mentions[i] + "\",");
			}
			mention = mention.concat("]");
		}
	}

	public void getTweetUrls() {
		int countTweetUrls = (cleanText.length() - cleanText.replace("http", "").length()) / 4;
		String[] tweetUrls = new String[countTweetUrls];
		for (int i = 0; i < countTweetUrls; i++) {
			tweetUrls[i] = cleanText.substring(cleanText.indexOf("http", 0),
					cleanText.indexOf(' ', cleanText.indexOf("http", 0)));
			cleanText = cleanText.replaceFirst(tweetUrls[i], "");
		}

		if (countTweetUrls == 0) {
			tweetUrl = "\"NULL\"";
		} else if (countTweetUrls == 1) {
			tweetUrl = "\"" + tweetUrls[0] + "\"";
		} else if (countTweetUrls > 1) {
			tweetUrl = "[";
			for (int i = 0; i < countTweetUrls; i++) {
				if (i == countTweetUrls - 1)
					tweetUrl = tweetUrl.concat("\"" + tweetUrls[i] + "\"");

				else
					tweetUrl = tweetUrl.concat("\"" + tweetUrls[i] + "\",");
			}
			tweetUrl = tweetUrl.concat("]");
		}
	}

	public void getEmoticons() {
		ArrayList<String> emoticons = new ArrayList<String>();

		String[] Kamoji = { "﻿¢‿¢", "©¿© o", "ª{•̃̾_•̃̾}ª", "¬_¬", "¯＼(º_o)/¯",
				// "¯\(º o)/¯",
				// "¯\_(⊙︿⊙)_/¯",
				// "¯\_(ツ)_/¯",
				"°ω°", "°Д°", "°‿‿°", "°ﺑ°", "´ ▽ ` )ﾉ", "¿ⓧ_ⓧﮌ", "Ò,ó", "ó‿ó", "ô⌐ô", "ôヮô", "ŎםŎ", "ŏﺡó", "ʕ•̫͡•ʔ",
				"ʕ•ᴥ•ʔ", "ʘ‿ʘ", "˚•_•˚", "˚⌇˚", "˚▱˚",
				// "̿ ̿̿'̿'\̵͇̿̿\=(•̪●)=/̵͇̿̿/'̿̿ ̿ ̿ ̿",
				"͡° ͜ʖ﻿ ͡°", "Σ ◕ ◡ ◕", "Σ (ﾟДﾟ;）", "Σ(ﾟДﾟ；≡；ﾟдﾟ)", "Σ(ﾟДﾟ )", "Σ(||ﾟДﾟ)", "Φ,Φ", "δﺡό", "σ_σ", "д_д",
				"ф_ф", "щ（ﾟДﾟщ）", "щ(ಠ益ಠщ)", "щ(ಥДಥщ)", "Ծ_Ծ", "أ‿أ", "ب_ب", "ح˚௰˚づ", "ح˚ᆺ˚ว", "حᇂﮌᇂ)", "٩๏̯͡๏۶",
				"٩๏̯͡๏)۶", "٩◔̯◔۶", "٩(×̯×)۶", "٩(̾●̮̮̃̾•̃̾)۶", "٩(͡๏̯͡๏)۶", "٩(͡๏̯ ͡๏)۶", "٩(ಥ_ಥ)۶", "٩(•̮̮̃•̃)۶",
				"٩(●̮̮̃•̃)۶", "٩(●̮̮̃●̃)۶", "٩(｡͡•‿•｡)۶", "٩(-̮̮̃•̃)۶", "٩(-̮̮̃-̃)۶", "۞_۞", "۞_۟۞", "۹ↁﮌↁ", "۹⌤_⌤۹",
				"॓_॔", "१✌◡✌५", "१|˚–˚|५", "ਉ_ਉ", "ଘ_ଘ", "இ_இ", "ఠ_ఠ", "రృర", "ಠ¿ಠi", "ಠ‿ಠ", "ಠ⌣ಠ", "ಠ╭╮ಠ", "ಠ▃ಠ",
				"ಠ◡ಠ", "ಠ益ಠ", "ಠ益ಠ", "ಠ︵ಠ凸", "ಠ , ಥ", "ಠ.ಠ", "ಠoಠ", "ಠ_ృ", "ಠ_ಠ", "ಠ_๏", "ಠ~ಠ", "ಡ_ಡ", "ತಎತ", "ತ_ತ",
				"ಥдಥ", "ಥ‿ಥ", "ಥ⌣ಥ", "ಥ◡ಥ", "ಥ﹏ಥ", "ಥ_ಥ ", "ಭ_ಭ", "ರ_ರ", "ಸ , ໖", "ಸ_ಸ", "ക_ക", "อ้_อ้", "อ_อ",
				"โ๏௰๏ใ ื", "๏̯͡๏﴿", "๏̯͡๏", "๏̯͡๏﴿", "๏[-ิิ_•ิ]๏", "๏_๏", "໖_໖", "༺‿༻", "ლ(´ڡ`ლ)", "ლ(́◉◞౪◟◉‵ლ)",
				"ლ(ಠ益ಠლ)", "ლ(╹◡╹ლ)", "ლ(◉◞౪◟◉‵ლ)", "ლ,ᔑ•ﺪ͟͠•ᔐ.ლ", "ᄽὁȍ ̪ őὀᄿ", "ᕕ( ᐛ )ᕗ", "ᕙ(⇀‸↼‶)ᕗ", "ᕦ(ò_óˇ)ᕤ",
				"ᶘ ᵒᴥᵒᶅ", "‘︿’", "•▱•", "•✞_✞•", "•ﺑ•", "•(⌚_⌚)•", "•_•)", "‷̗ↂ凸ↂ‴̖", "‹•.•›", "‹› ‹(•¿•)› ‹›",
				"‹(ᵒᴥᵒ­­­­­)›﻿", "‹(•¿•)›", "ↁ_ↁ", "⇎_⇎", "∩(︶▽︶)∩", "∩( ・ω・)∩", "≖‿≖", "≧ヮ≦", "⊂•⊃_⊂•⊃", "⊂⌒~⊃｡Д｡)⊃",
				"⊂(◉‿◉)つ", "⊂(ﾟДﾟ,,⊂⌒｀つ", "⊙ω⊙", "⊙▂⊙", "⊙▃⊙", "⊙△⊙", "⊙︿⊙", "⊙﹏⊙", "⊙０⊙", "⊛ठ̯⊛", "⋋ō_ō`",
				"━━━ヽ(ヽ(ﾟヽ(ﾟ∀ヽ(ﾟ∀ﾟヽ(ﾟ∀ﾟ)ﾉﾟ∀ﾟ)ﾉ∀ﾟ)ﾉﾟ)ﾉ)ﾉ━━━", "┌∩┐(◕_◕)┌∩┐", "┌( ಠ_ಠ)┘", "┌( ಥ_ಥ)┘", "╚(•⌂•)╝",
				"╭╮╭╮☜{•̃̾_•̃̾}☞╭╮╭╮", "╭✬⌢✬╮", "╮(─▽─)╭", "╯‵Д′)╯彡┻━┻", "╰☆╮", "□_□", "►_◄", "◃┆◉◡◉┆▷", "◉△◉", "◉︵◉",
				"◉_◉", "○_○",
				// "●¿●\ ~",
				"●_●", "◔̯◔", "◔ᴗ◔", "◔ ⌣ ◔", "◔_◔", "◕ω◕", "◕‿◕", "◕◡◕", "◕ ◡ ◕", "◖♪_♪|◗", "◖|◔◡◉|◗", "◘_◘", "◙‿◙",
				"◜㍕◝", "◪_◪", "◮_◮", "☁ ☝ˆ~ˆ☂", "☆¸☆", "☉‿⊙", "☉_☉", "☐_☐", "☜ق❂Ⴢ❂ق☞", "☜(⌒▽⌒)☞", "☜(ﾟヮﾟ☜)",
				"☜-(ΘLΘ)-☞", "☝☞✌", "☮▁▂▃▄☾ ♛ ◡ ♛ ☽▄▃▂▁☮", "☹_☹", "☻_☻", "☼.☼", "☾˙❀‿❀˙☽", "♀ح♀ヾ", "♥‿♥", "♥╣[-_-]╠♥",
				"♥╭╮♥", "♥◡♥", "✌♫♪˙❤‿❤˙♫♪✌", "✌.ʕʘ‿ʘʔ.✌", "✌.|•͡˘‿•͡˘|.✌", "✖‿✖", "✖_✖", "❐‿❑", "⨀_⨀", "⨀_Ꙩ", "⨂_⨂",
				"〆(・∀・＠)", "《〠_〠》", "【•】_【•】", "〠_〠", "〴⋋_⋌〵", "のヮの", "ニガー? ━━━━━━(ﾟ∀ﾟ)━━━━━━ ニガー?",
				// "ペ㍕˚\",
				"ヽ(´ｰ｀ )ﾉ", "ヽ(๏∀๏ )ﾉ", "ヽ(｀Д´)ﾉ", "ヽ(ｏ`皿′ｏ)ﾉ", "ヽ(`Д´)ﾉ", "ㅎ_ㅎ", "乂◜◬◝乂", "凸ಠ益ಠ)凸", "句_句", "Ꙩ⌵Ꙩ",
				"Ꙩ_Ꙩ", "ꙩ_ꙩ", "Ꙫ_Ꙫ", "ꙫ_ꙫ", "ꙮ_ꙮ", "흫_흫", "句_句", "﴾͡๏̯͡๏﴿ O'RLY?",
				// "﻿¯\(ºдಠ)/¯",
				"（·×·）", "（⌒Д⌒）", "（╹ェ╹）", "（♯・∀・）⊃", "（　´∀｀）☆", "（　´∀｀）", "（゜Д゜）", "（・∀・）", "（・Ａ・）", "（ﾟ∀ﾟ）", "（￣へ￣）",
				"（ ´☣///_ゝ///☣｀）", "（ つ Д ｀）", "＿☆（ ´_⊃｀）☆＿", "｡◕‿‿◕｡", "｡◕ ‿ ◕｡", "!⑈ˆ~ˆ!⑈", "!(｀･ω･｡)", "(¬‿¬)",
				"(¬▂¬)", "(¬_¬)", "(°ℇ °)", "(°∀°)", "(´ω｀)", "(´◉◞౪◟◉)", "(´ヘ｀;)", "(´・ω・｀)", "(´ー｀)", "(ʘ‿ʘ)",
				"(ʘ_ʘ)", "(˚இ˚)", "(͡๏̯͡๏)", "(ΘεΘ;)", "(ι´Д｀)ﾉ", "(Ծ‸ Ծ)", "(॓_॔)", "(० ्०)", "(ு८ு_ .:)", "(ಠ‾ಠ﻿)",
				"(ಠ‿ʘ)", "(ಠ‿ಠ)", "(ಠ⌣ಠ)", "(ಠ益ಠ ╬)", "(ಠ益ಠ)", "(ಠ_ృ)", "(ಠ_ಠ)", "(ಥ﹏ಥ)", "(ಥ_ಥ)", "(๏̯͡๏ )",
				"(ღˇ◡ˇ)~♥", "(ღ˘⌣˘ღ) ♫･*:.｡. .｡.:*･", "(ღ˘⌣˘ღ)", "(ᵔᴥᵔ)", "(•ω•)", "(•‿•)", "(•⊙ω⊙•)", "(• ε •)",
				"(∩▂∩)", "(∩︵∩)", "(∪ ◡ ∪)", "(≧ω≦)", "(≧◡≦)", "(≧ロ≦)", "(⊙ヮ⊙)", "(⊙_◎)", "(⋋▂⋌)", "(⌐■_■)", "(─‿‿─)",
				"(┛◉Д◉)┛┻━┻", "(╥_╥)", "(╬ಠ益ಠ)", "(╬◣д◢)", "(╬ ಠ益ಠ)", "(╯°□°）╯︵ ┻━┻", "(╯ಊ╰)", "(╯◕_◕)╯", "(╯︵╰,)",
				"(╯3╰)", "(╯_╰)", "(╹◡╹)凸", "(▰˘◡˘▰)", "(●´ω｀●)", "(●´∀｀●)", "(◑‿◐)", "(◑◡◑)", "(◕‿◕✿)", "(◕‿◕)",
				"(◕‿-)", "(◕︵◕)", "(◕ ^ ◕)", "(◕_◕)", "(◜௰◝)", "(◡‿◡✿)", "(◣_◢)", "(☞ﾟ∀ﾟ)☞", "(☞ﾟヮﾟ)☞", "(☞ﾟ ∀ﾟ )☞",
				"(☼◡☼)", "(☼_☼)", "(✌ﾟ∀ﾟ)☞", "(✖╭╮✖)", "(✪㉨✪)", "(✿◠‿◠)", "(✿ ♥‿♥)", "(　・∀・)", "(　･ัω･ั)？",
				"(　ﾟ∀ﾟ)o彡゜えーりんえーりん!!", "(。・_・。)", "(つд｀)", "(づ｡◕‿‿◕｡)づ", "(ノಠ益ಠ)ノ彡┻━┻", "(ノ ◑‿◑)ノ", "(ノ_・。)", "(・∀・ )",
				"(屮ﾟДﾟ)屮", "(︶ω︶)", "(︶︹︺)", "(ﺧ益ﺨ)", "(；一_一)", "(｀・ω・´)”", "(｡◕‿‿◕｡)", "(｡◕‿◕｡)", "(｡◕ ‿ ◕｡)",
				"(｡♥‿♥｡)", "(｡･ω..･)っ", "(･ｪ-)", "(ﾉ◕ヮ◕)ﾉ*:・ﾟ✧", "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧", "(ﾟДﾟ)", "(ﾟДﾟ)y─┛~~", "(ﾟ∀ﾟ)",
				"(ﾟヮﾟ)", "(￣□￣)", "(￣。￣)", "(￣ー￣)", "(￣(エ)￣)", "( °٢° )", "( ´_ゝ｀)", "( ͡° ͜ʖ ͡°)", "( ͡~ ͜ʖ ͡°)",
				"( ಠ◡ಠ )", "( •_•)>⌐■-■", "( 　ﾟ,_ゝﾟ)", "( ･ิз･ิ)", "( ﾟдﾟ)､", "( ^▽^)σ)~O~)", "((((゜д゜;))))", "(*´д｀*)",
				"(*..Д｀)", "(*..д｀*)", "(*~▽~)", "(-’๏_๏’-)", "(-＿- )ノ", "(/◔ ◡ ◔)/", "(///_ಥ)", "(;´Д`)", "(=ω=;)",
				"(=゜ω゜)", "(>'o')> ♥ <('o'<)", "(n˘v˘•)¬", "(o´ω｀o)", "(V)(°,,°)(V)",
				// "(\/) (°,,°) (\/)",
				"(^▽^)", "(`･ω･´)", "(~￣▽￣)~",
				// "/╲/\╭ºoꍘoº╮/\╱\",
				"<【☯】‿【☯】>", "=(ﾟдﾟ)ｳ", "@_@", "d(*⌒▽⌒*)b", "o(≧∀≦)o", "o(≧o≦)o", "q(❂‿❂)p", "y=ｰ( ﾟдﾟ)･∵.",
				// "\˚ㄥ˚\",
				// "\ᇂ_ᇂ\",
				// "\(ಠ ὡ ಠ )/",
				// "\(◕ ◡ ◕\)",
				"^̮^", "^ㅂ^", "_(͡๏̯͡๏)_", "{´◕ ◡ ◕｀}", "{ಠ_ಠ}__,,|,", "{◕ ◡ ◕}" };

		for (int j = 0; j < Kamoji.length; j++) {
			if (cleanText.contains(Kamoji[j]) && Kamoji[j].length() != 0) {
				emoticons.add(Kamoji[j]);
				cleanText = cleanText.replace(Kamoji[j], " ");
			}
		}

		for (int l = 0; l < cleanText.length(); l++) {
			if ((int) cleanText.charAt(l) >= 8230 && (int) cleanText.charAt(l) <= 13055) {
				emoticons.add(cleanText.substring(l, l + 1));
				cleanText = (cleanText.substring(0, l)) + " " + cleanText.substring(l + 1, cleanText.length());
				l = l - 1;
			}

			else if ((int) cleanText.charAt(l) == 55356) {
				if (((int) cleanText.charAt(l + 1) >= 56324 && (int) cleanText.charAt(l + 1) <= 56527)
						|| ((int) cleanText.charAt(l + 1) >= 56688 && (int) cleanText.charAt(l + 1) <= 56913)
						|| ((int) cleanText.charAt(l + 1) >= 57088 && (int) cleanText.charAt(l + 1) <= 57328)) {
					emoticons.add(cleanText.substring(l, l + 2));
					cleanText = (cleanText.substring(0, l)) + " " + cleanText.substring(l + 2, cleanText.length());
					l = l - 1;
				}
			}

			else if ((int) cleanText.charAt(l) == 55357) {
				if (((int) cleanText.charAt(l + 1) >= 56332 && (int) cleanText.charAt(l + 1) <= 56911)
						|| ((int) cleanText.charAt(l + 1) >= 56960 && (int) cleanText.charAt(l + 1) <= 57029)) {
					emoticons.add(cleanText.substring(l, l + 2));
					cleanText = (cleanText.substring(0, l)) + " " + cleanText.substring(l + 2, cleanText.length());
					l = l - 1;
				}
			}
		}

		/*
		 * for (int i = 0; i < emoticons.size(); i++) { tweetEmoticons =
		 * tweetEmoticons + (emoticons.get(i)); }
		 */

		if (emoticons.size() == 0) {
			tweetEmoticons = "\"NULL\"";
		} else if (emoticons.size() == 1) {
			tweetEmoticons = "\"" + emoticons.get(0) + "\"";
		} else if (emoticons.size() > 1) {
			tweetEmoticons = "[";
			for (int i = 0; i < emoticons.size(); i++) {
				if (i == emoticons.size() - 1)
					tweetEmoticons = tweetEmoticons.concat("\"" + emoticons.get(i) + "\"");

				else
					tweetEmoticons = tweetEmoticons.concat("\"" + emoticons.get(i) + "\",");
			}
			tweetEmoticons = tweetEmoticons.concat("]");
		}

	}
}

public class fetch_tweets {

	public static Status status_update(Configuration c1) throws Exception {
		Twitter twitter = new TwitterFactory(c1).getInstance();
		Status status = twitter.updateStatus("Hi3!");
		System.out.println("Successfully updated the status to [" + status.getText() + "].");
		return status;
	}

	public static Configuration ConfigurationBuilderMethod(String a, String b, String c, String d) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setJSONStoreEnabled(true);
		cb.setOAuthConsumerKey(a);
		cb.setOAuthConsumerSecret(b);
		cb.setOAuthAccessToken(c);
		cb.setOAuthAccessTokenSecret(d);
		Configuration configuration = cb.build();
		return configuration;
	}

	public static String toNearestWholeHour(LocalDateTime d) {
		LocalDateTime ldt = d;

		if (ldt.getMinute() >= 30)
			ldt = ldt.plusHours(1);

		ldt = ldt.withMinute(0);
		ldt = ldt.withSecond(0);
		DateTimeFormatter iso_instant = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String text = ldt.format(iso_instant);
		return text;
	}

	public static void getTweets(Configuration c1, String[] term, String[] lang) throws InterruptedException,
			ParseException, IOException, PatternSyntaxException, StringIndexOutOfBoundsException {
		File file = new File("C:\\Users\\Junaid\\Documents\\JSON\\News_ko.json");
		file.createNewFile();
		FileWriter writer = new FileWriter(file, true);
		long lastTweetId = Long.MAX_VALUE;
		int i = 0, j = 0, r = 0, numberOfQueries = 0;
		int[] numberOfResults = new int[100];
		String text_en = null, text_es = null, text_ko = null, text_tr = null;
		LocalDateTime createdAt;
		String Dates[] = new String[] { "2016-09-19", "2016-09-18", "2016-09-17", "2016-09-16", "2016-09-15",
				"2016-09-14", "2016-09-13", "2016-09-12", "2016-09-11", "2016-09-10" };

		/*
		 * textCleaner tc1 = new
		 * textCleaner("I'm sick of the news. I'm sick of them not demanding Trump & his surrogates stop lying. Stop letting these people come on the air & lie!"
		 * ); String tweetUrls = textCleaner.tweetUrl; String mentions =
		 * textCleaner.mention; String hashTags = textCleaner.hashTag; String
		 * cleanText = textCleaner.cleanText; String tweetEmojis =
		 * textCleaner.tweetEmoticons;
		 * System.out.println(tweetUrls+mentions+hashTags+tweetEmojis+cleanText)
		 * ;
		 */

		for (int d = 0; d < Dates.length - 1; d++) {
			for (int t = 0; t < term.length && numberOfQueries < 180; t++) {
				// System.out.println(term[t]);
				for (int l = 0; l < lang.length && numberOfQueries < 180; l++) {
					// System.out.println(lang[l]);
					Query query = new Query(term[t]);
					query.lang(lang[l]);
					query.count(100);
					query.setMaxId(Long.MAX_VALUE);
					System.out.println(lastTweetId);
					query.setSince(Dates[d + 1]);
					query.setUntil(Dates[d]);
					while (((numberOfResults[r] <= 3000 && (lang[l] == "ko" || lang[l] == "tr"))
							|| (numberOfResults[r] <= 2000 && (lang[l] == "en" || lang[l] == "es")))
							&& numberOfQueries < 180) {
						{
							try {
								Twitter twitter = new TwitterFactory(c1).getInstance();
								QueryResult result = twitter.search(query);
								numberOfQueries++;
								// System.out.println(result.getTweet
								// s().size());

								if (result.getTweets().size() == 0) {
									System.out.println("breaking");
									break;
								}

								numberOfResults[r] += result.getTweets().size();
								for (Status status : result.getTweets()) {
									System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
									String json;
									if (status.getId() < lastTweetId)
										lastTweetId = status.getId();
									textCleaner tc = new textCleaner(status.getText());
									createdAt = LocalDateTime.ofInstant(status.getCreatedAt().toInstant(),
											ZoneId.systemDefault());
									String geoLocation;

									if (status.getGeoLocation() == null)
										geoLocation = "\"NULL\"";
									else
										geoLocation = "\"" + status.getGeoLocation().getLatitude() + ","
												+ status.getGeoLocation().getLongitude() + "\"";

									switch (status.getLang()) {
									case "en": {
										text_en = textCleaner.cleanText;
										text_es = "\"NULL\"";
										text_ko = "\"NULL\"";
										text_tr = "\"NULL\"";
										break;
									}
									case "es": {
										text_en = "\"NULL\"";
										text_es = textCleaner.cleanText;
										text_ko = "\"NULL\"";
										text_tr = "\"NULL\"";
										break;
									}
									case "ko": {
										text_en = "\"NULL\"";
										text_es = "\"NULL\"";
										text_ko = textCleaner.cleanText;
										text_tr = "\"NULL\"";
										break;
									}
									case "tr": {
										text_en = "\"NULL\"";
										text_es = "\"NULL\"";
										text_ko = "\"NULL\"";
										text_tr = textCleaner.cleanText;
										break;
									}
									}

									json = "{\"tweet_id\":\"" + status.getId() + "\",\"topic\":\"" + "News"
											+ "\",\"tweet_text\":\"" + status.getText().replaceAll("[\\\"]", "")
											+ "\",\"tweet_lang\":\"" + status.getLang() + "\",\"text_en\":" + text_en
											+ ",\"text_es\":" + text_es + ",\"text_ko\":" + text_ko + ",\"text_tr\":"
											+ text_tr + ",\"hashtags\":" + textCleaner.hashTag + ",\"mentions\":"
											+ textCleaner.mention + ",\"tweet_urls\":" + textCleaner.tweetUrl
											+ ",\"tweet_emoticons\":" + textCleaner.tweetEmoticons
											+ ",\"tweet_date\":\"" + toNearestWholeHour(createdAt) + "\",\"tweet_loc\":"
											+ geoLocation + "}\n";

									writer.write(json);
								}
								if (numberOfQueries == 180) {
									r = 0;
									for (int d1 = 0; d1 < Dates.length - 1; d1++) {
										System.out.println("Date: " + Dates[d1 + 1]);
										for (String t1 : term) {
											for (String l1 : lang) {
												System.out.println("Number of tweets for: News in language: " + l1
														+ " = " + numberOfResults[r++]);
											}
										}
									}
									return;

									/*
									 * System.out.println("Sleeping");
									 * TimeUnit.MINUTES.sleep(15);
									 * System.out.println("Awake");
									 * numberOfQueries = 0;
									 */
								}
							} catch (Exception te) {
								System.out.println("Failed to search tweets: " + te.getMessage());
								query.setMaxId(lastTweetId - 1);
							}
							query.setMaxId(lastTweetId - 1);

						}

					}
					r++;
				}
			}
		}

		writer.flush();
		writer.close();
		r = 0;
		for (int d1 = 0; d1 < Dates.length - 1; d1++) {
			System.out.println("Date: " + Dates[d1 + 1]);
			for (String t1 : term) {
				for (String l1 : lang) {
					System.out.println("Number of tweets for: News in language: " + l1 + " = " + numberOfResults[r++]);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String a = new String("GPvyG4q80YKeSGby9xNLC90GE");
		String b = new String("zEJFvxGFJH1QtnhqoaEC7bI4MDG2dyUNbbN0UcuIiBAN9F7Rtm");
		String c = new String("773728170847731712-opaaa1PE9IU7ezFe6RWjFUIHaKb2cAo");
		String d = new String("ncrgvWQzfzKwv4LCGmU7GWMnlYu6sEvstvXzKDCQbGa96");

		String e = new String("i3dC5bYk1nBjKDDeILBIg9cla");
		String f = new String("BUZFFABIVOwyVakPou8Pe1FB6eY3xd8TB1OVEcjW3GMh5AdfQ3");
		String g = new String("773728170847731712-zjha1eJ4LcXyIF86g0z1djyX6XoeJ84");
		String h = new String("q2O7eYHKApJvSUuFS8z4GvQKnvSSsFPbNXjcLdkZFjdmJ");

		String i = new String("5BM5TVcpXi40quQmu7BPZz2Nz");
		String j = new String("YyFTM92cdgeaVasWzoEh9XFMiK5HcgxbFyn2YdgZsMzaw9DLx1");
		String k = new String("773728170847731712-Q6wQK4DSdWfpN1Cr3EmlPmTEHQEz1YW");
		String l = new String("p4ESzIufndHa7yb3OOZOjewjbIOOmUWvdM9PpYllJFzqL");

		String m = new String("Uw5FaGIaz18dRCzxYSXkkUORW");
		String n = new String("tWqM3a1PWq5Pe86xgZj1S8PyyeI2bFGswbfS168ojD5rRl2TVk");
		String o = new String("773728170847731712-20Mj3pDzURh45ZTXtmtoR8wqmYfz8F2");
		String p = new String("09PVRWptGBeBvzv2tgKvGIo8wBqDnUSmDYRGxCcubI38i");

		String a1 = new String("5ezYRdkNgExtUBb5vWYXzee7d");
		String b1 = new String("cVz9xcVrSNA3vtO4sgfSueXxc1V1dMnCVG4yQANLq8nd0ORYnr");
		String c1 = new String("778105859486408705-pcf6scsPGThGjws7xwEZcwn0uuHDGmH");
		String d1 = new String("qI5Dd4uiiTloKW5FFnhYLPtPIO2rMYpq7Kbd1K3EnxoQh");

		Configuration[] configuration = new Configuration[8];
		configuration[0] = ConfigurationBuilderMethod(e, f, g, h);
		configuration[1] = ConfigurationBuilderMethod(a, b, c, d);
		configuration[2] = ConfigurationBuilderMethod(i, j, k, l);
		configuration[3] = ConfigurationBuilderMethod(m, n, o, p);
		configuration[4] = ConfigurationBuilderMethod(a1, b1, c1, d1);

		// status_update(configuration);
		// String[] topics = new String[] {"(USOpen) OR (Kerber) OR (Wawrinka)
		// OR (Djokovic) OR (Monfils) OR (Nadal) OR (US 오픈) +exclude:retweets"};
		// String[] topics = new String[] {"(Game of Thrones) OR (GameOfThrones)
		// OR (Jon Snow) OR (George Martin) OR (Tyrion) OR (Targaryen) OR
		// (Lannister) " + " OR (왕좌의 게임) OR (존 스노우) OR (조지 마틴) OR (타르 가르 옌) " +
		// " +exclude:retweets" };
		// String[] topics = new String[] {"(iphone7) OR (iphone 7) OR (airpods)
		// OR (Apple Keynote) " + " OR (아이폰 7) OR (아이폰(7)) OR (애플 시계 시리즈 2) OR
		// (아이폰 OS (10)) " +" OR (Keynote de apple) OR (Apple Seguir Serie 2)
		// "+" OR (Apple Ürünü Serisi 2) "+" +exclude:retweets" };
		// String[] topics = new String[] {"(USPresidentialElections) OR (2016
		// Election) OR (US President) OR (Trump) OR (Melania Trump) OR (Ivanka
		// Trump) OR (US Election Polls) " + " OR (Las elecciones presidenciales
		// de Estados Unidos) OR (Elección 2016) OR (El presidente de EE UU) OR
		// (Encuestas electorales de EE.UU) "+" OR (2016 선거)" +" OR (ABD
		// Başkanlık Seçimleri) OR (2016 Seçim) OR (ABD Başkanı) OR (ABD Seçim
		// Anketleri) "+" +exclude:retweets" };
		String[] topics = new String[] {
				"(Syrian War) OR (#SyrianWar) OR (#Syria) OR (시리아 전쟁) OR (시리아) OR (시리아 공격) OR (Syrian Civil War) OR (시리아 내전) "
						+ " OR (Suriye Savaşı) OR (Suriye) OR (Suriye İç Savaşı) OR (Suriye Saldırı) "
						+ " OR (Guerra Siria) OR (Guerra civil siria) OR (Ataque sirio) +exclude:retweets" };
		String[] lang = new String[] { "ko" };
		getTweets(configuration[0], topics, lang);
	}
}

# check-dictionary
# 形態素解析の辞書の解析

LuceneのKuromojiに利用できる辞書かどうかを判断する基準がいくつかあるので、
辞書のCSVファイルを元に、判断に合わないデータが有るかどうかをチェックするためにちょっとだけ書いたプログラム。

https://search-tech.connpass.com/event/146365/
この勉強会に参加して、UniDicのビルドの確認のためにサクッと作ったツールです。

現在3つのツールがあります。

## AnalyzeDictionary

UniDicのCSVファイルをKuromojiに適用する場合に、適したデータではないものが存在しないかをチェックする。

## CountPunctuationCharacter

ipadicのCSVファイル内に、Kuromojiが[Punctuation(区切り文字)と判断している](https://github.com/apache/lucene-solr/blob/master/lucene/analysis/kuromoji/src/java/org/apache/lucene/analysis/ja/JapaneseTokenizer.java#L1897) 単語がどのくらい存在しているかをチェックする。
最初の文字が区切り文字、全てが区切り文字(1文字以上の単語で)、最初以外の場所に区切り文字、の3つの種類を数えます。
1単語はいずれかの1つに数えています。

結果 mecab-ipadic-2.7.0-20070801 : 

* 最初の1文字目が区切り文字 : 104単語(うち2文字以上の長さのものは7単語)
* すべてが区切り文字 : 0単語
* 先頭以外に区切り文字が出てくるもの : 723単語 

[Gistに一覧を貼っておきました。](https://gist.github.com/johtani/50aa2776a385c5c8dfa3a0d1e4e268cd)

結果 UniDic 2.1.2:

* 最初の1文字目が区切り文字 : 717単語(うち384文字以上の長さのものは7単語)
* すべてが区切り文字 : 0単語
* 先頭以外に区切り文字が出てくるもの : 1780単語 

[Gistに一覧を貼っておきました。](https://gist.github.com/johtani/3769639bc24ebeab17ddcb1be039ba94)

## AnalyzeSample

BuildしたKuromojiの2種類のjar（ipadicとunidic）を使用して、同じ文章をTokenizeして標準出力に出力する。

#### 利用方法

1. Luceneのソースをhttps://github.com/johtani/lucene-solr/tree/fix-4056からcloneする（LUCENE-4056がまだパッチの段階のため）
2. Luceneのjarをビルド（IPADICのライブラリをビルド）
3. lucene-core-9.0.0-SNAPSHOT.jar、lucene-analyzers-common-9.0.0-SNAPSHOT.jarをtarget_jarsにコピーする
4. lucene-analyzers-kuromoji-9.0.0-SNAPSHOT.jarをlucene-analyzers-kuromoji-ipadic-9.0.0-SNAPSHOT.jar
5. Luceneをunidicでビルド（参考：https://gist.github.com/johtani/91cfd2753aba2e001c1d39f47666ada7#file-build-xml）
6. lucene-analyzers-kuromoji-9.0.0-SNAPSHOT.jarをlucene-analyzers-kuromoji-unidic-9.0.0-SNAPSHOT.jar
7. AnalyzeSampleを実行する



# License

MITライセンス
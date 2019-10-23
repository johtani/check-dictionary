# check-dictionary
# 形態素解析の辞書の解析

LuceneのKuromojiに利用できる辞書かどうかを判断する基準がいくつかあるので、
辞書のCSVファイルを元に、判断に合わないデータが有るかどうかをチェックするためにちょっとだけ書いたプログラム。

https://search-tech.connpass.com/event/146365/
この勉強会に参加して、UniDicのビルドの確認のためにサクッと作ったツールです。

現在2つのツールがあります。

## AnalyzeDictionary

UniDicのCSVファイルをKuromojiに適用する場合に、適したデータではないものが存在しないかをチェックする。

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
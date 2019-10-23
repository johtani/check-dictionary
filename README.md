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

# License

MITライセンス
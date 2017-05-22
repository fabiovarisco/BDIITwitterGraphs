Para rodar aplicacao:
Clicar com o direito no projeto > Run As > Maven build... > no campo "Goals", colocar "spring-boot::run" > Run
A aplicacao vai inicar na porta 8080 por padrao. Nao tem UI, eh uma aplicacao REST so. 


Para popular o banco: 

Para comecar, se usa a seguinte chamada, que vai salvar o usuario, seus primeiros tweets e, para os tweets que foram retweetados, vai ser salvo tbm o retweet e o usuario que retweetou. Por exemplo, comecando com o usuario "Twitter": 

http://localhost:8080/twitter/populate/user?username=Twitter

Para popular os followers do usuario "Twitter": 

http://localhost:8080/twitter/populate/user/followers?username=Twitter


Entao, pode-se usar a seguinte chamada para obter e salvar no DB os followers de todos usuarios salvos na primeira chamada (que retweetaram tweets do Twitter): 

http://localhost:8080/twitter/populate/AllUserFollowers


Ainda pode-se usar a seguinte chamada para obter e salvar os tweets (incluindo Retweets e quem retweetou) recentes de todos usuarios salvos no DB:

http://localhost:8080/twitter/populate/AllUsersTweets


OBS: As duas ultimas chamadas vao estourar o limite de requests que a API do Twitter permite. Entao, os dados so serao recuperados para os primeiros usuarios. Mas, eh mais do que suficiente para demonstrar o DB. Veja os rate limits da API: 

https://dev.twitter.com/rest/public/rate-limits

Como o limite eh pra cada 15 minutos, eh so esperar 15 minutos pra fazer o request de novo. 


Queries: 

- Visualizar os tweets do usuario Twitter que foram retweetados
match (n:Profile{screenName:"Twitter"})-[:TWEETED]-(t:Tweet)-[:RETWEETED]-(rt:Tweet) return n, t, rt

- Visualizar os tweets do usuario Twitter que foram retweetados e por quem foram retweetados (permite visualizar se um tweet foi retweetado pelo mesmo usuario)
match (n:Profile{screenName:"Twitter"})-[:TWEETED]-(t:Tweet)-[:RETWEETED]-(rt:Tweet)-[:TWEETED_BY]-(p:Profile) return n, t, rt, p

- Visualizar seguidores do Twitter
match (n:Profile{screenName:"Twitter"})-[:FOLLOWED_BY]-(f:Profile) return n, f

Adicionar limit 100 a qualquer query limita o numero de resultados a 100.



Para deletar tudo do banco (nodes e relationships): 
MATCH (N) DETACH DELETE N

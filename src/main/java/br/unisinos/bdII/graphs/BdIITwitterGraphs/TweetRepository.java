package br.unisinos.bdII.graphs.BdIITwitterGraphs;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends Neo4jRepository<Tweet, Long>{
	
	public Tweet findByTwitterId(String twitterId);
	
}

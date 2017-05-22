package br.unisinos.bdII.graphs.BdIITwitterGraphs;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends Neo4jRepository<Profile, Long>{

	public Profile findByTwitterId(long twitterId);
	public Profile findByScreenName(String screenName);
}

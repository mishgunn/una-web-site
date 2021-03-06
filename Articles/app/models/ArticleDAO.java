package models;

import java.util.*;
import java.util.Date;
import play.modules.mongodb.jackson.MongoDB;
import net.vz.mongodb.jackson.*;
import org.codehaus.jackson.annotate.JsonProperty;

public class ArticleDAO{
	
	private static JacksonDBCollection<Article, String> coll = MongoDB.getCollection("articles", Article.class, String.class);

	
	/* Basic methods to handle DAO
	 * 
	 */
	
	// Method to collect all articles from DataBase
	public static List<Article> all() {
	    return sort(coll.find().toArray());
	}

	// Method to add an article to the DataBase
	public static void create(Article article) {
		coll.save(article);
	}

	// Method to delete an article from the DataBase
	public static void delete(String id) {
	    Article article = coll.findOneById(id);
	    if (article != null)
	        coll.remove(article);
	}
	
	
	/* Methods more specific below
	 * 
	 */
	
	//Sorts out a List of Article according to the Article publicationDate
	//Using Insertion Sort algorithm
	public static List<Article> sort(List<Article> articleList){
		Article intermediaire;
		int j = 0;
		for(int i=articleList.size()-2; i>=0; i--){
			intermediaire = articleList.get(i);
			j = i;
			while((j<articleList.size()-1)&&(articleList.get(j+1).getPublicationDate().compareTo(intermediaire.getPublicationDate())>0)){
				articleList.set(j, articleList.get(j+1));
				j = j+1;
			}
			articleList.set(j, intermediaire);
		}
		return articleList;
	}
	
	// Get all the Articles written by this specific User
	public static List<Article> all(User author){
		return sort(coll.find(DBQuery.is("author",author)).toArray());
	}
	
	//Get the Article corresponding to the Id in argument
	public static Article getById(String id){
		return coll.findOneById(id);
	}
	
	//Get the Article fitting the specified date and author
	//Necessary to be able to search for an Article which was just added to the database, without knowing its Id
	public static Article getByDateAndAuthor(Date date, User author){
		return coll.findOne(DBQuery.and(DBQuery.is("publicationDate",date), DBQuery.is("author",author)));
	}
	
	//Allow to update an existing Article, adding a new commentary to its commentaries List
	public static void addCommentary(String articleId, String commentaryId){
		Article article = getById(articleId); //Create the Article which will be kept
		article.addCommentary(CommentaryDAO.getById(commentaryId)); //Update the final Article commentaries
		coll.update(getById(articleId),article); //Replace the existing Article by the final one in the database
	}
	
	//Allow to update an existing Article, removing the specified commentary from its commentaries List
	public static void removeCommentary(String articleId, String commentaryId){
		Article article = getById(articleId); //Create the Article which will be kept
		article.removeCommentary(CommentaryDAO.getById(commentaryId)); //Update the final Article commentaries
		coll.update(getById(articleId),article); //Replace the existing Article by the final one in the database
	}
	
	//Allows to update a given Article after having set some of its fields
	public static void updateArticle(Article articleToBeModified){
		coll.update(getById(articleToBeModified.getId()),articleToBeModified);
	}
}
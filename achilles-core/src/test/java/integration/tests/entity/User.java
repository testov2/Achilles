package integration.tests.entity;

import info.archinnov.achilles.type.WideMap;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

/**
 * User
 * 
 * @author DuyHai DOAN
 * 
 */
@Entity
public class User implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column
	private String firstname;

	@Column
	private String lastname;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinColumn(table = "user_tweets")
	private WideMap<Integer, Tweet> tweets;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinColumn(table = "user_timeline")
	private WideMap<Long, Tweet> timeline;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(table = "retweets_cf")
	private WideMap<Integer, Tweet> retweets;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn
	private User referrer;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}

	public WideMap<Integer, Tweet> getTweets()
	{
		return tweets;
	}

	public WideMap<Long, Tweet> getTimeline()
	{
		return timeline;
	}

	public WideMap<Integer, Tweet> getRetweets()
	{
		return retweets;
	}

	public User getReferrer()
	{
		return referrer;
	}

	public void setReferrer(User referrer)
	{
		this.referrer = referrer;
	}
}

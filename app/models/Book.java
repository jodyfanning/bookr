package models;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import play.data.validation.Constraints.Required;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity(value = "books", concern = "FSYNC_SAFE", noClassnameStored = true)
public final class Book {

	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	@Id
	private ObjectId id = ObjectId.get();

	@Required
	private String title;

	private String author;
	private String isbn;
	private String publisher;
	private String language;
	private String pages;
	private String publisheddate;
	private String publishedplace;
	private String series;
	private String originaltitle;
	private String translator;
	private String source;
	private int version = 1;

	public static final List<String> properties = Arrays.asList("title", "author", "isbn", "publisher", "language", "pages",
			"publisheddate", "publishedplace", "series", "originaltitle", "translator", "source");

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((originaltitle == null) ? 0 : originaltitle.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		result = prime * result + ((publisheddate == null) ? 0 : publisheddate.hashCode());
		result = prime * result + ((publishedplace == null) ? 0 : publishedplace.hashCode());
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((series == null) ? 0 : series.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((translator == null) ? 0 : translator.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		} else if (!isbn.equals(other.isbn))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (originaltitle == null) {
			if (other.originaltitle != null)
				return false;
		} else if (!originaltitle.equals(other.originaltitle))
			return false;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		if (publisheddate == null) {
			if (other.publisheddate != null)
				return false;
		} else if (!publisheddate.equals(other.publisheddate))
			return false;
		if (publishedplace == null) {
			if (other.publishedplace != null)
				return false;
		} else if (!publishedplace.equals(other.publishedplace))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (series == null) {
			if (other.series != null)
				return false;
		} else if (!series.equals(other.series))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (translator == null) {
			if (other.translator != null)
				return false;
		} else if (!translator.equals(other.translator))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Book [hashCode()=");
		builder.append(hashCode());
		builder.append(", ");
		if (getId() != null) {
			builder.append("getId()=");
			builder.append(getId());
			builder.append(", ");
		}
		if (getTitle() != null) {
			builder.append("getTitle()=");
			builder.append(getTitle());
			builder.append(", ");
		}
		if (getAuthor() != null) {
			builder.append("getAuthor()=");
			builder.append(getAuthor());
			builder.append(", ");
		}
		if (getIsbn() != null) {
			builder.append("getIsbn()=");
			builder.append(getIsbn());
			builder.append(", ");
		}
		if (getPublisher() != null) {
			builder.append("getPublisher()=");
			builder.append(getPublisher());
			builder.append(", ");
		}
		if (getLanguage() != null) {
			builder.append("getLanguage()=");
			builder.append(getLanguage());
			builder.append(", ");
		}
		if (getPages() != null) {
			builder.append("getPages()=");
			builder.append(getPages());
			builder.append(", ");
		}
		if (getPublisheddate() != null) {
			builder.append("getPublisheddate()=");
			builder.append(getPublisheddate());
			builder.append(", ");
		}
		if (getPublishedplace() != null) {
			builder.append("getPublishedplace()=");
			builder.append(getPublishedplace());
			builder.append(", ");
		}
		if (getSeries() != null) {
			builder.append("getSeries()=");
			builder.append(getSeries());
			builder.append(", ");
		}
		if (getOriginaltitle() != null) {
			builder.append("getOriginaltitle()=");
			builder.append(getOriginaltitle());
			builder.append(", ");
		}
		if (getTranslator() != null) {
			builder.append("getTranslator()=");
			builder.append(getTranslator());
			builder.append(", ");
		}
		if (getSource() != null) {
			builder.append("getSource()=");
			builder.append(getSource());
			builder.append(", ");
		}
		builder.append("getVersion()=");
		builder.append(getVersion());
		builder.append("]");
		return builder.toString();
	}

	public Book() {
	}

	public Book(String title) {
		this.setTitle(title);
	}

	public Book(ObjectId id, String title) {
		this(title);
		this.setId(id);
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getPublisheddate() {
		return publisheddate;
	}

	public void setPublisheddate(String publisheddate) {
		this.publisheddate = publisheddate;
	}

	public String getPublishedplace() {
		return publishedplace;
	}

	public void setPublishedplace(String publishedplace) {
		this.publishedplace = publishedplace;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getOriginaltitle() {
		return originaltitle;
	}

	public void setOriginaltitle(String originaltitle) {
		this.originaltitle = originaltitle;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}

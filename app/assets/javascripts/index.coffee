$ -> $.get "/sentiments", (sentiments) -> $.each sentiments, (index, sentiment) -> $("#sentiments").append $("<li>").text sentiment.content + " : " + sentiment.probability
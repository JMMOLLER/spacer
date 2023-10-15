export default async function handler(request, response) {
  try {
    const result = await fetch(
      'https://spacer-api.onrender.com/api',
    );

    if(!result.ok) {
      throw new Error(result.statusText);
    }

    const data = await result.json();
 
    if (data.statusCode === 200) {
      console.log(data.response);
      response
        .status(200)
        .json({ message: data.description });
    } else {
      console.log(data.response);
      response.status(500).json({ error: data.description });
    }
  } catch (error) {
    console.error('Error fetching spacer api:', error);
    response.status(500).json({ error: 'Failed to fetch spacer api' });
  }
}
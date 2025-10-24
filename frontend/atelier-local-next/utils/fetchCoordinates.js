const fetchCoordinates = async (address) => {
  if (!address) return { latitude: null, longitude: null };

  try {
    const response = await fetch(`${process.env._API_URL}/api/geocode`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ address }),
    });

    if (!response.ok) throw new Error("Erreur geocoding");

    const data = await response.json();
    return data; // { latitude, longitude }
  } catch (err) {
    console.error("Erreur geocoding :", err);
    return { latitude: null, longitude: null };
  }
};

export default fetchCoordinates;
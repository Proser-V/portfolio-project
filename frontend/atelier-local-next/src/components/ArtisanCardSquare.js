export default function ArtisanCardSquare({ name, job, phone, email, avatar }) {
  return (
    <div className="bg-white shadow-md border border-amber-200 rounded-lg p-4 flex flex-col items-center">
      <img src={avatar} alt={name} className="h-48 w-full object-cover mb-4 rounded" />
      <div className="text-indigo-950 text-lg font-bold text-center">{name}</div>
      <div className="text-indigo-950 text-center">{job}</div>
      <div className="text-indigo-950 text-center">{phone}</div>
      <div className="text-indigo-950 text-center">{email}</div>
    </div>
  );
}